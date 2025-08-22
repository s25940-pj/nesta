package com.example.nesta.fixtures;

import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.flextrade.jfixture.JFixture;

import java.time.LocalDateTime;

public class MoveInApplicationFixtures {
    private static final JFixture fixture;

    static {
        fixture = new JFixture();
        fixture.customise().circularDependencyBehaviour().omitSpecimen();
    }

    public static MoveInApplication pendingMoveInApplication() {
        MoveInApplication moveInApplication = new MoveInApplication();

        moveInApplication.setRentalOffer(RentalOfferFixtures.rentalOffer(ApartmentFixtures.apartment()));

        moveInApplication.setViewingDateTime(LocalDateTime.now().plusDays(1));
        moveInApplication.setLandlordStatus(MoveInApplicationStatus.PENDING);
        moveInApplication.setRentierStatus(MoveInApplicationStatus.PENDING);

        return moveInApplication;
    }
}
