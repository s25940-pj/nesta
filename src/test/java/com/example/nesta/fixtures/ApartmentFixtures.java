package com.example.nesta.fixtures;

import com.example.nesta.model.Apartment;
import com.example.nesta.model.enums.ParkingType;

public class ApartmentFixtures {
    public static Apartment validApartment() {
        Apartment apartment = new Apartment();
        apartment.setNumberOfRooms(2);
        apartment.setNumberOfBathrooms(1);
        apartment.setFloor(1);
        apartment.setFurnished(true);
        apartment.setHasBalcony(true);
        apartment.setParkingType(ParkingType.STREET);
        apartment.setHasElevator(true);
        apartment.setDisabledAccessible(false);
        apartment.setHasStorageRoomInBasement(false);
        apartment.setArea(40);
        apartment.setStreetName("Main");
        apartment.setBuildingNumber("12");
        apartment.setApartmentNumber("5A");
        apartment.setPostalCode("10001");
        apartment.setCity("Springville");
        apartment.setCountry("Poland");

        return apartment;
    }
}
