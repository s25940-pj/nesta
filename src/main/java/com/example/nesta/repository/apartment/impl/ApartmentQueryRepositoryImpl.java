package com.example.nesta.repository.apartment.impl;

import com.example.nesta.dto.apartment.ApartmentFilter;
import com.example.nesta.model.Apartment;
import com.example.nesta.model.QApartment;
import com.example.nesta.model.enums.ParkingType;
import com.example.nesta.repository.apartment.ApartmentQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApartmentQueryRepositoryImpl implements ApartmentQueryRepository {
    private final JPAQueryFactory queryFactory;

    public ApartmentQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Apartment> searchApartments(ApartmentFilter filter) {
        QApartment apartment = QApartment.apartment;

        return queryFactory
                .selectFrom(apartment)
                .where(
                        filter.getNumberOfRooms() != null ? apartment.numberOfRooms.eq(filter.getNumberOfRooms()) : null,
                        filter.getNumberOfBathrooms() != null ? apartment.numberOfBathrooms.eq(filter.getNumberOfBathrooms()) : null,
                        filter.getFloor() != null ? apartment.floor.eq(filter.getFloor()) : null,
                        filter.getFurnished() != null ? apartment.furnished.eq(filter.getFurnished()) : null,
                        filter.getHasBalcony() != null ? apartment.hasBalcony.eq(filter.getHasBalcony()) : null,
                        filter.getParkingType() != null ? apartment.parkingType.eq(filter.getParkingType()) : null,
                        filter.getHasElevator() != null ? apartment.hasElevator.eq(filter.getHasElevator()) : null,
                        filter.getIsDisabledAccessible() != null ? apartment.isDisabledAccessible.eq(filter.getIsDisabledAccessible()) : null,
                        filter.getHasStorageRoomInBasement() != null ? apartment.hasStorageRoomInBasement.eq(filter.getHasStorageRoomInBasement()) : null
                )
                .fetch();
    }
}
