package com.example.nesta.service.moveinapplication;

import com.example.nesta.dto.moveinapplication.DecisionRequest;
import com.example.nesta.dto.moveinapplication.RescheduleRequest;
import com.example.nesta.exception.common.InvalidReferenceException;
import com.example.nesta.exception.moveinapplication.*;
import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.example.nesta.repository.moveinapplication.MoveInApplicationRepository;
import com.example.nesta.utils.JwtUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MoveInApplicationService {
    private final MoveInApplicationRepository moveInApplicationRepository;

    public MoveInApplicationService(MoveInApplicationRepository moveInApplicationRepository) {
        this.moveInApplicationRepository = moveInApplicationRepository;
    }

    public MoveInApplication create(MoveInApplication moveInApplication, Jwt jwt) {
        long rentalOfferId = 0;

        try {
            rentalOfferId = moveInApplication.getRentalOffer().getId();
            var viewingDateTime = moveInApplication.getViewingDateTime();

            validateViewingDateIsAvailable(rentalOfferId, viewingDateTime);
            validateViewingDateInFuture(viewingDateTime);

            String rentierId = jwt.getSubject();
            boolean rentierAlreadyHasActiveApplicationForThisOffer = moveInApplicationRepository.existsRentierPendingByRentalOfferAndRentierId(rentalOfferId, rentierId);

            if (rentierAlreadyHasActiveApplicationForThisOffer) {
                throw new ActiveApplicationAlreadyExistsException();
            }

            moveInApplication.setRentierId(rentierId);

            return moveInApplicationRepository.save(moveInApplication);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains(String.format("Key (rental_offer_id)=(%d) is not present in table \"rental_offer\"", moveInApplication.getRentalOffer().getId()))) {
                throw new InvalidReferenceException(String.format("Rental offer with id %d does not exist.", rentalOfferId));
            }

            throw e;
        }
    }

    public void rescheduleViewing(Long id, RescheduleRequest request, Jwt jwt) {
        var moveInApplication = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        JwtUtils.requireOwner(jwt, moveInApplication.getRentierId());

        if (moveInApplication.getLandlordStatus() != MoveInApplicationStatus.PENDING) {
            throw new ViewingRescheduleNotAllowedException();
        }

        var rentalOfferId = moveInApplication.getRentalOffer().getId();
        var updatedViewingDateTime = request.updatedViewingDateTime();

        if (request.updatedViewingDateTime().equals(moveInApplication.getViewingDateTime())) {
            throw new ViewingDateUnchangedException();
        }

        validateViewingDateIsAvailable(rentalOfferId, updatedViewingDateTime);
        validateViewingDateInFuture(updatedViewingDateTime);
        moveInApplication.setViewingDateTime(updatedViewingDateTime);
        moveInApplicationRepository.save(moveInApplication);
    }

    private void validateViewingDateIsAvailable(long rentalOfferId, LocalDateTime viewingDateTime) {
        var taken = moveInApplicationRepository.existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(rentalOfferId, viewingDateTime);

        if (taken) {
            throw new ViewingDateNotAvailableException("The selected viewing date is already reserved by another active move-in application");
        }
    }

    private void validateViewingDateInFuture(LocalDateTime viewingDateTime) {
        if (viewingDateTime.isBefore(LocalDateTime.now())) {
            throw new ViewingDateNotAvailableException("Viewing date must be in the future");
        }
    }

    public MoveInApplication getMoveInApplicationById(Long id, Jwt jwt) {
        var moveInApplication = moveInApplicationRepository.findById(id).orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        try {
            JwtUtils.requireOwner(jwt, moveInApplication.getRentierId());
        } catch (ResponseStatusException ex) {
            JwtUtils.requireOwner(jwt, moveInApplication.getRentalOffer().getLandlordId());
        }

        return moveInApplication;
    }

    public List<MoveInApplication> getMoveInApplicationsByRentierId(String rentierId, Jwt jwt) {
        JwtUtils.requireOwner(jwt, rentierId);

        return moveInApplicationRepository.findAllByRentierId(rentierId);
    }

    public List<MoveInApplication> getMoveInApplicationsByLandlordId(String landlordId, Jwt jwt) {
        JwtUtils.requireOwner(jwt, landlordId);

        return moveInApplicationRepository.findAllByRentalOffer_LandlordId(landlordId);
    }

    public void setDecision(Long id, DecisionRequest request, Jwt jwt) {
        MoveInApplication moveInApplication = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        try {
            JwtUtils.requireOwner(jwt, moveInApplication.getRentierId());
        } catch (ResponseStatusException ex) {
            JwtUtils.requireOwner(jwt, moveInApplication.getRentalOffer().getLandlordId());
        }

        var roles = JwtUtils.getRoles(jwt);

        if (roles.contains("LANDLORD")) {
            moveInApplication.setLandlordDecidedAt(LocalDateTime.now());
            moveInApplication.setLandlordStatus(request.status());
            moveInApplication.setLandlordDecisionReason(request.reason());
        } else if (roles.contains("RENTIER")) {
            var rentierDecision = request.status();

            if (rentierDecision != MoveInApplicationStatus.CANCELLED) validateLandlordHasLeftDecision(moveInApplication);

            moveInApplication.setRentierDecidedAt(LocalDateTime.now());
            moveInApplication.setRentierStatus(request.status());
            moveInApplication.setRentierDecisionReason(request.reason());
        }

        moveInApplicationRepository.save(moveInApplication);
    }

    private void validateLandlordHasLeftDecision(MoveInApplication application) {
        if (application.getLandlordStatus() != MoveInApplicationStatus.APPROVED) {
            throw new LandlordDecisionRequiredException();
        }
    }
}
