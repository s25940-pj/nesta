package com.example.nesta.repository.moveinapplication;

import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface MoveInApplicationRepository extends JpaRepository<MoveInApplication, Long> {
    boolean existsByRentalOffer_IdAndViewingDateTimeAndLandlordStatusIn(
            Long rentalOfferId,
            LocalDateTime viewingDateTime,
            Collection<MoveInApplicationStatus> statuses
    );
    boolean existsByRentalOffer_IdAndViewingDateTimeAndRentierStatusIn(
            Long rentalOfferId,
            LocalDateTime viewingDateTime,
            Collection<MoveInApplicationStatus> statuses
    );
    boolean existsByRentalOffer_IdAndRentierIdAndRentierStatusIn(Long rentalOfferId, String rentierId, Collection<MoveInApplicationStatus> statuses);
    List<MoveInApplication> findAllByRentierId(String rentierId);
    List<MoveInApplication> findAllByRentalOffer_LandlordId(String landlordId);
}
