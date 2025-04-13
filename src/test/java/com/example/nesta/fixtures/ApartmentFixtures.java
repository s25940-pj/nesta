package com.example.nesta.fixtures;

import com.example.nesta.model.Address;
import com.example.nesta.model.Apartment;
import com.example.nesta.model.enums.ParkingType;

public class ApartmentFixtures {
    public static Apartment validApartment() {
        Address address = new Address();
        address.setStreetName("Main");
        address.setBuildingNumber("12");
        address.setApartmentNumber("5A");
        address.setPostalCode("10001");
        address.setCity("Springville");
        address.setCountry("Poland");

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
        apartment.setAddress(address);

        return apartment;
    }
}
