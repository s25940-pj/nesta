package com.example.nesta.repository.rentaloffer;

import com.example.nesta.model.RentalOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalOfferRepository extends JpaRepository<RentalOffer, Long>, RentalOfferQueryRepository {
    Optional<RentalOffer> findByApartmentId(Long apartmentId);
}
