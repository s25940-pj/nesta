package com.example.nesta.fixtures;

import com.example.nesta.model.Apartment;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.model.enums.EmploymentStatus;
import com.example.nesta.model.enums.FurnishingStatus;
import com.example.nesta.model.enums.PetPolicy;
import com.example.nesta.model.enums.SmokingPolicy;
import com.flextrade.jfixture.JFixture;

import java.math.BigDecimal;

public class RentalOfferFixtures {
    private static final JFixture fixture;

    static {
        fixture = new JFixture();
        fixture.customise().circularDependencyBehaviour().omitSpecimen();
    }
    public static RentalOffer rentalOffer(Apartment apartment) {
        RentalOffer rentalOffer = new RentalOffer();

        rentalOffer.setLandlordId(fixture.create(String.class));
        rentalOffer.setApartment(apartment);
        rentalOffer.setMonthlyRent(fixture.create(BigDecimal.class));
        rentalOffer.setDeposit(fixture.create(BigDecimal.class));
        rentalOffer.setUtilitiesCost(fixture.create(BigDecimal.class));
        rentalOffer.setUtilitiesIncluded(fixture.create(Boolean.class));
        rentalOffer.setAvailableFrom(fixture.create(java.time.LocalDate.class));
        rentalOffer.setAvailableUntil(fixture.create(java.time.LocalDate.class));
        rentalOffer.setShortTermRental(fixture.create(Boolean.class));
        rentalOffer.setFurnishingStatus(fixture.create(FurnishingStatus.class));
        rentalOffer.setPreferredEmploymentStatus(fixture.create(EmploymentStatus.class));
        rentalOffer.setSmokingPolicy(fixture.create(SmokingPolicy.class));
        rentalOffer.setPetPolicy(fixture.create(PetPolicy.class));
        rentalOffer.setAccessibleForDisabled(fixture.create(Boolean.class));

        return rentalOffer;
    }
}
