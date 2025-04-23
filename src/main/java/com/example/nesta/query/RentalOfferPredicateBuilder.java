package com.example.nesta.query;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.QRentalOffer;
import com.querydsl.core.types.dsl.BooleanExpression;

public class RentalOfferPredicateBuilder {

    public static BooleanExpression[] build(RentalOfferFilter filter, QRentalOffer rentalOffer) {
        if (filter == null) return new BooleanExpression[]{};

        return new BooleanExpression[]{
                filter.getMonthlyRent() != null ? rentalOffer.monthlyRent.eq(filter.getMonthlyRent()) : null,
                filter.getDeposit() != null ? rentalOffer.deposit.eq(filter.getDeposit()) : null,
                filter.getUtilitiesCost() != null ? rentalOffer.utilitiesCost.eq(filter.getUtilitiesCost()) : null,
                filter.getUtilitiesIncluded() != null ? rentalOffer.utilitiesIncluded.eq(filter.getUtilitiesIncluded()) : null,
                filter.getAvailableFrom() != null ? rentalOffer.availableFrom.eq(filter.getAvailableFrom()) : null,
                filter.getAvailableUntil() != null ? rentalOffer.availableUntil.eq(filter.getAvailableUntil()) : null,
                filter.getShortTermRental() != null ? rentalOffer.shortTermRental.eq(filter.getShortTermRental()) : null,
                filter.getFurnishingStatus() != null ? rentalOffer.furnishingStatus.eq(filter.getFurnishingStatus()) : null,
                filter.getPetPolicy() != null ? rentalOffer.petPolicy.eq(filter.getPetPolicy()) : null,
                filter.getSmokingPolicy() != null ? rentalOffer.smokingPolicy.eq(filter.getSmokingPolicy()) : null,
                filter.getPreferredEmploymentStatus() != null ? rentalOffer.preferredEmploymentStatus.eq(filter.getPreferredEmploymentStatus()) : null,
                filter.getShortTermRental() != null ? rentalOffer.shortTermRental.eq(filter.getShortTermRental()) : null,
                filter.getAccessibleForDisabled() != null ? rentalOffer.accessibleForDisabled.eq(filter.getAccessibleForDisabled()) : null,
        };
    }
}
