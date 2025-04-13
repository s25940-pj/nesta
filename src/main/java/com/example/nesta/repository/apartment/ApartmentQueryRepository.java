package com.example.nesta.repository.apartment;

import com.example.nesta.dto.apartment.ApartmentFilter;
import com.example.nesta.model.Apartment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentQueryRepository {
    List<Apartment> searchApartments(ApartmentFilter filter);
}
