package com.example.nesta.service.moveinapplication;

import com.example.nesta.dto.moveinapplication.DecisionRequest;
import com.example.nesta.dto.moveinapplication.RescheduleRequest;
import com.example.nesta.exception.moveinapplication.*;
import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.example.nesta.repository.moveinapplication.MoveInApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoveInApplicationService {
    private final MoveInApplicationRepository moveInApplicationRepository;

    private static final List<MoveInApplicationStatus> BLOCKING_STATUSES =
            List.of(MoveInApplicationStatus.PENDING, MoveInApplicationStatus.APPROVED);

    public MoveInApplicationService(MoveInApplicationRepository moveInApplicationRepository) {
        this.moveInApplicationRepository = moveInApplicationRepository;
    }

    public MoveInApplication create(MoveInApplication moveInApplication, Jwt jwt) {
        var rentalOfferId = moveInApplication.getRentalOffer().getId();
        var viewingDateTime = moveInApplication.getViewingDateTime();

        validateViewingDateIsAvailable(rentalOfferId, viewingDateTime);
        validateViewingDateInFuture(viewingDateTime);

        String rentierId = jwt.getSubject();
        boolean rentierAlreadyHasActiveApplication = moveInApplicationRepository.existsByRentalOffer_IdAndRentierIdAndStatusIn(
                rentalOfferId, rentierId, BLOCKING_STATUSES);

        if  (rentierAlreadyHasActiveApplication) {
            throw new ActiveApplicationAlreadyExistsException("You already have an active application for this offer.");
        }

        moveInApplication.setRentierId(rentierId);

        return  moveInApplicationRepository.save(moveInApplication);
    }

    public void rescheduleViewing(Long id, RescheduleRequest request) {
        var moveInApplication = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        ensureApplicationPending(moveInApplication);

        var rentalOfferId = moveInApplication.getRentalOffer().getId();
        var updatedViewingDateTime = request.updatedViewingDateTime();

        if (request.updatedViewingDateTime().equals(moveInApplication.getViewingDateTime())) {
            throw new ViewingDateUnchangedException("New viewing date must be different from the current one.");
        }

        validateViewingDateIsAvailable(rentalOfferId, updatedViewingDateTime);
        validateViewingDateInFuture(updatedViewingDateTime);
        moveInApplication.setViewingDateTime(updatedViewingDateTime);
        moveInApplicationRepository.save(moveInApplication);
    }

    private void ensureApplicationPending(MoveInApplication application) {
        if (application.getLandlordStatus() != MoveInApplicationStatus.PENDING &&
                application.getRentierStatus() != MoveInApplicationStatus.PENDING) {
            throw new MoveInApplicationAlreadyClosedException(application.getId());
        }
    }

    private void validateViewingDateInFuture(LocalDateTime viewingDateTime) {
        if (viewingDateTime.isBefore(LocalDateTime.now())) {
            throw new ViewingDateNotAvailableException("Viewing date must be in the future");
        }
    }

    private void validateViewingDateIsAvailable(long rentalOfferId, LocalDateTime viewingDateTime) {
        var taken = moveInApplicationRepository.existsByRentalOffer_IdAndViewingDateTimeAndStatusIn(rentalOfferId, viewingDateTime, BLOCKING_STATUSES);

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
        List<String> roles = jwt.getClaimAsStringList("roles");

        MoveInApplication application = moveInApplicationRepository.findById(id)
                .orElseThrow(() -> new MoveInApplicationNotFoundException(id));

        if (roles.contains("LANDLORD")) {
            application.setLandlordDecidedAt(LocalDateTime.now());
            application.setLandlordStatus(request.status());
            application.setLandlordDecisionReason(request.reason());
        } else if (roles.contains("RENTIER")) {
            application.setRentierDecidedAt(LocalDateTime.now());
            application.setRentierStatus(request.status());
            application.setRentierDecisionReason(request.reason());
        }

        moveInApplicationRepository.save(application);
    }
}
