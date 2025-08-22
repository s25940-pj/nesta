package com.example.nesta.repository.moveinapplication;

import com.example.nesta.model.MoveInApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MoveInApplicationRepository extends JpaRepository<MoveInApplication, Long> {
    @Query("""
   SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
   FROM MoveInApplication m
   WHERE m.rentalOffer.id = :rentalOfferId
     AND m.viewingDateTime = :dt
     AND (m.rentierStatus = 'PENDING' OR m.landlordStatus = 'PENDING')
   """)
    boolean existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(
            @Param("rentalOfferId") Long rentalOfferId,
            @Param("dt") LocalDateTime dt
    );
    @Query("select case when count(a) > 0 then true else false end " +
            "from MoveInApplication a " +
            "where a.rentalOffer.id = :offerId " +
            "and a.rentierId = :rentierId " +
            "and a.rentierStatus = 'PENDING'")
    boolean existsRentierPendingByRentalOfferAndRentierId(@Param("offerId") Long offerId,
                                                          @Param("rentierId") String rentierId);

    List<MoveInApplication> findAllByRentierId(String rentierId);
    List<MoveInApplication> findAllByRentalOffer_LandlordId(String landlordId);
}
