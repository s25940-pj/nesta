package com.example.nesta.dto;

import com.example.nesta.model.enums.ParkingType;
import lombok.Data;

@Data
public class ApartmentFilter {
    private Integer numberOfRooms;
    private Integer numberOfBathrooms;
    private Integer floor;
    private Boolean furnished;
    private Boolean hasBalcony;
    private ParkingType parkingType;
    private Boolean hasElevator;
    private Boolean isDisabledAccessible;
    private Boolean hasStorageRoomInBasement;
}
