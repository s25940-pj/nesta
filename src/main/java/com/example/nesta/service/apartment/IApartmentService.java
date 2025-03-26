package com.example.nesta.service.apartment;

import com.example.nesta.model.Apartment;

import java.util.List;
import java.util.Optional;

public interface IApartmentService {
    Apartment createApartment(Apartment apartment);
    Optional<Apartment> getApartmentById(Long id);
    List<Apartment> getAllApartments();
    Apartment updateApartment(Long id, Apartment apartment);
    void deleteApartment(Long id);
}
