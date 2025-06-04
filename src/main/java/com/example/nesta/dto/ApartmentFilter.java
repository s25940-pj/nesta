package com.example.nesta.dto;

import com.example.nesta.model.enums.ParkingType;
import lombok.Data;

@Data
public class ApartmentFilter {
    private String landlordId;
    private Integer area;
    private Integer numberOfRooms;
    private Integer numberOfBathrooms;
    private Integer floor;
    private Boolean furnished;
    private Boolean hasBalcony;
    private ParkingType parkingType;
    private Boolean hasElevator;
    private Boolean isDisabledAccessible;
    private Boolean hasStorageRoomInBasement;
    private String streetName;
    private String buildingNumber;
    private String apartmentNumber;
    private String city;
    private String postalCode;
    private String country;
}
