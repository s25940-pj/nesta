package com.example.nesta.repository.rentaloffer;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.RentalOffer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalOfferQueryRepository {
    List<RentalOffer> searchRentalOffers(RentalOfferFilter filter);
}
