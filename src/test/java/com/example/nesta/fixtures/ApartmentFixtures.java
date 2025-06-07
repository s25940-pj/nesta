package com.example.nesta.fixtures;

import com.example.nesta.model.Apartment;
import com.example.nesta.model.enums.ParkingType;
import com.flextrade.jfixture.JFixture;

public class ApartmentFixtures {
    private static final JFixture fixture;

    static {
        fixture = new JFixture();
        fixture.customise().circularDependencyBehaviour().omitSpecimen();
    }

    public static Apartment apartment() {
        Apartment apartment = new Apartment();

        apartment.setLandlordId(fixture.create(String.class));
        apartment.setNumberOfRooms(fixture.create(Integer.class));
        apartment.setNumberOfBathrooms(fixture.create(Integer.class));
        apartment.setFloor(fixture.create(Integer.class));
        apartment.setFurnished(fixture.create(Boolean.class));
        apartment.setHasBalcony(fixture.create(Boolean.class));
        apartment.setParkingType(fixture.create(ParkingType.class));
        apartment.setHasElevator(fixture.create(Boolean.class));
        apartment.setDisabledAccessible(fixture.create(Boolean.class));
        apartment.setHasStorageRoomInBasement(fixture.create(Boolean.class));
        apartment.setArea(fixture.create(Integer.class));
        apartment.setStreetName(fixture.create(String.class));
        apartment.setBuildingNumber(fixture.create(String.class));
        apartment.setApartmentNumber(fixture.create(String.class));
        apartment.setPostalCode(fixture.create(String.class));
        apartment.setCity(fixture.create(String.class));
        apartment.setCountry(fixture.create(String.class));

        return apartment;
    }
}
