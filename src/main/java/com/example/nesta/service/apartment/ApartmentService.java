package com.example.nesta.service.apartment;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.exception.apartment.ApartmentAlreadyExistsForAddressException;
import com.example.nesta.exception.apartment.ApartmentNotFoundException;
import com.example.nesta.model.Apartment;
import com.example.nesta.repository.apartment.ApartmentRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    public Apartment createApartment(Apartment apartment, Jwt jwt) {
        if (apartmentAlreadyExistsForAddress(apartment)) {
            throw new ApartmentAlreadyExistsForAddressException("An apartment already exists for the given address.");
        }
        String userId = jwt.getSubject();
        apartment.setUserId(userId);

        return apartmentRepository.save(apartment);
    }

    private boolean apartmentAlreadyExistsForAddress(Apartment apartment) {
        return apartmentRepository.findByStreetNameAndBuildingNumberAndApartmentNumberAndCityAndPostalCodeAndCountry(
                apartment.getStreetName(),
                apartment.getBuildingNumber(),
                apartment.getApartmentNumber(),
                apartment.getCity(),
                apartment.getPostalCode(),
                apartment.getCountry()
        ).isPresent();
    }

    public Optional<Apartment> getApartmentById(Long apartmentId) {
        return apartmentRepository.findById(apartmentId);
    }

    public List<Apartment> getAllApartments(Jwt jwt) {
        String userId = jwt.getSubject();

        return apartmentRepository.getAllApartmentsByUserId(userId);
    }
    
    public Apartment updateApartment(Long id, Apartment updatedApartment) {
        return apartmentRepository.findById(id)
                .map(existing -> {
                    updatedApartment.setId(id);
                    return apartmentRepository.save(updatedApartment);
                })
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public void deleteApartment(Long id) {
        if (!apartmentRepository.existsById(id)) {
            throw new ApartmentNotFoundException(id);
        }
        apartmentRepository.deleteById(id);
    }

    public List<Apartment> searchApartments(ApartmentFilter filter) {
        return apartmentRepository.searchApartments(filter);
    }
}
