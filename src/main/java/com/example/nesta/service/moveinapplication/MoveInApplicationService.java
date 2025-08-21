package com.example.nesta.service.moveinapplication;

import com.example.nesta.dto.moveinapplication.DecisionRequest;
import com.example.nesta.dto.moveinapplication.RescheduleRequest;
import com.example.nesta.exception.common.InvalidReferenceException;
import com.example.nesta.exception.common.InvalidRoleException;
import com.example.nesta.exception.moveinapplication.*;
import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.example.nesta.repository.moveinapplication.MoveInApplicationRepository;
import com.example.nesta.utils.JwtUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MoveInApplicationService {
    private final MoveInApplicationRepository moveInApplicationRepository;

    private static final List<MoveInApplicationStatus> BLOCKING_STATUSES =
            List.of(MoveInApplicationStatus.PENDING, MoveInApplicationStatus.APPROVED);

    public MoveInApplicationService(MoveInApplicationRepository moveInApplicationRepository) {
        this.moveInApplicationRepository = moveInApplicationRepository;
    }

    public MoveInApplication create(MoveInApplication moveInApplication, Jwt jwt) {
        long rentalOfferId = 0;

        try {
            rentalOfferId = moveInApplication.getRentalOffer().getId();
            var viewingDateTime = moveInApplication.getViewingDateTime();

            validateViewingDateIsAvailable(rentalOfferId, viewingDateTime, jwt);
            validateViewingDateInFuture(viewingDateTime);

            String rentierId = jwt.getSubject();
            boolean rentierAlreadyHasActiveApplicationForThisOffer = moveInApplicationRepository.existsByRentalOffer_IdAndRentierIdAndRentierStatusIn(
                    rentalOfferId, rentierId, BLOCKING_STATUSES);

            if (rentierAlreadyHasActiveApplicationForThisOffer) {
                throw new ActiveApplicationAlreadyExistsException();
            }

            moveInApplication.setRentierId(rentierId);

            return moveInApplicationRepository.save(moveInApplication);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Key (rental_offer_id)=(" + rentalOfferId + ") is not present in table \"rental_offer\"")) {
                throw new InvalidReferenceException("Rental offer with id" + rentalOfferId + "does not exist.");
            }

            throw e;
        }
    }

    public void rescheduleViewing(Long id, RescheduleRequest request, Jwt jwt) {
        var moveInApplication = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        ensureApplicationPending(moveInApplication);

        var rentalOfferId = moveInApplication.getRentalOffer().getId();
        var updatedViewingDateTime = request.updatedViewingDateTime();

        if (request.updatedViewingDateTime().equals(moveInApplication.getViewingDateTime())) {
            throw new ViewingDateUnchangedException();
        }

        validateViewingDateIsAvailable(rentalOfferId, updatedViewingDateTime, jwt);
        validateViewingDateInFuture(updatedViewingDateTime);
        moveInApplication.setViewingDateTime(updatedViewingDateTime);
        moveInApplicationRepository.save(moveInApplication);
    }

    private void ensureApplicationPending(MoveInApplication application) {
        if (application.getLandlordStatus() != MoveInApplicationStatus.PENDING ||
                application.getRentierStatus() != MoveInApplicationStatus.PENDING) {
            throw new MoveInApplicationAlreadyClosedException(application.getId());
        }
    }

    private void validateViewingDateInFuture(LocalDateTime viewingDateTime) {
        if (viewingDateTime.isBefore(LocalDateTime.now())) {
            throw new ViewingDateNotAvailableException("Viewing date must be in the future");
        }
    }

    private void validateViewingDateIsAvailable(long rentalOfferId, LocalDateTime viewingDateTime, Jwt jwt) {
        var roles = JwtUtils.getRoles(jwt);
        var taken = false;

        if (roles.contains("LANDLORD")) {
            taken = moveInApplicationRepository.existsByRentalOffer_IdAndViewingDateTimeAndLandlordStatusIn(rentalOfferId, viewingDateTime, BLOCKING_STATUSES);
        } else if (roles.contains("RENTIER")) {
            taken = moveInApplicationRepository.existsByRentalOffer_IdAndViewingDateTimeAndRentierStatusIn(rentalOfferId, viewingDateTime, BLOCKING_STATUSES);
        } else {
            throw new InvalidRoleException("Unknown role: access denied.");
        }

        if (taken) {
            throw new ViewingDateNotAvailableException("The selected viewing date is already taken for this offer.");
        }
    }

    public Optional<MoveInApplication> getMoveInApplicationById(Long id) { return moveInApplicationRepository.findById(id); }

    public List<MoveInApplication> getMoveInApplicationsByRentierId(String rentierId) {
        return moveInApplicationRepository.findAllByRentierId(rentierId);
    }

    public List<MoveInApplication> getMoveInApplicationsByLandlordId(String landlordId) {
        return moveInApplicationRepository.findAllByRentalOffer_LandlordId(landlordId);
    }

    public void setDecision(Long id, DecisionRequest request, Jwt jwt) {
        var roles = JwtUtils.getRoles(jwt);

        MoveInApplication application = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        if (roles.contains("LANDLORD")) {
            application.setLandlordDecidedAt(LocalDateTime.now());
            application.setLandlordStatus(request.status());
            application.setLandlordDecisionReason(request.reason());
        } else if (roles.contains("RENTIER")) {
            validateLandlordHasLeftDecision(application); // nie w przypadku cancelled
            application.setRentierDecidedAt(LocalDateTime.now());
            application.setRentierStatus(request.status());
            application.setRentierDecisionReason(request.reason());
        }

        moveInApplicationRepository.save(application);
    }

    private void validateLandlordHasLeftDecision(MoveInApplication application) {
        if (application.getLandlordStatus() != MoveInApplicationStatus.APPROVED) {
            throw new LandlordDecisionRequiredException();
        }
    }
}
