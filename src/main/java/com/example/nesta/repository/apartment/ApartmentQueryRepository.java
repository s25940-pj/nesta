package com.example.nesta.repository.apartment;

import com.example.nesta.dto.apartment.ApartmentFilter;
import com.example.nesta.model.Apartment;
import com.example.nesta.model.enums.ParkingType;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentQueryRepository {
    List<Apartment> searchApartments(ApartmentFilter filter);
}
