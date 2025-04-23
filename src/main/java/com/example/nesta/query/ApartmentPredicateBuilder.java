package com.example.nesta.query;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.model.QApartment;
import com.querydsl.core.types.dsl.BooleanExpression;

public class ApartmentPredicateBuilder {
    public static BooleanExpression[] build(ApartmentFilter filter, QApartment apartment) {
        if (filter == null) return new BooleanExpression[]{};

        return new BooleanExpression[]{
                filter.getNumberOfRooms() != null ? apartment.numberOfRooms.eq(filter.getNumberOfRooms()) : null,
                filter.getNumberOfBathrooms() != null ? apartment.numberOfBathrooms.eq(filter.getNumberOfBathrooms()) : null,
                filter.getFloor() != null ? apartment.floor.eq(filter.getFloor()) : null,
                filter.getFurnished() != null ? apartment.furnished.eq(filter.getFurnished()) : null,
                filter.getHasBalcony() != null ? apartment.hasBalcony.eq(filter.getHasBalcony()) : null,
                filter.getParkingType() != null ? apartment.parkingType.eq(filter.getParkingType()) : null,
                filter.getHasElevator() != null ? apartment.hasElevator.eq(filter.getHasElevator()) : null,
                filter.getIsDisabledAccessible() != null ? apartment.isDisabledAccessible.eq(filter.getIsDisabledAccessible()) : null,
                filter.getHasStorageRoomInBasement() != null ? apartment.hasStorageRoomInBasement.eq(filter.getHasStorageRoomInBasement()) : null
        };
    }
}
