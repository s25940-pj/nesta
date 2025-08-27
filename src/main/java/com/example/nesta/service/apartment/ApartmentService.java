package com.example.nesta.service.apartment;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.exception.apartment.ApartmentAlreadyExistsForAddressException;
import com.example.nesta.exception.apartment.ApartmentNotFoundException;
import com.example.nesta.model.Apartment;
import com.example.nesta.repository.apartment.ApartmentRepository;
import com.example.nesta.utils.JwtUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

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
        apartment.setLandlordId(userId);

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

    public Apartment getApartmentById(Long apartmentId, Jwt jwt) {
        var apartment = apartmentRepository.findById(apartmentId).orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

        JwtUtils.requireOwner(jwt, apartment.getLandlordId());

        return apartment;
    }

    public List<Apartment> getAllApartmentsByLandlordId(Jwt jwt) {
        String landlordId = jwt.getSubject();

        return apartmentRepository.getAllApartmentsByLandlordId(landlordId);
    }
    
    public Apartment updateApartment(Long id, Apartment updatedApartment, Jwt jwt) {
        return apartmentRepository.findById(id)
                .map(existing -> {
                    JwtUtils.requireOwner(jwt, existing.getLandlordId());

                    updatedApartment.setId(existing.getId());
                    updatedApartment.setLandlordId(existing.getLandlordId());
                    updatedApartment.setImages(existing.getImages());
                    return apartmentRepository.save(updatedApartment);
                })
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    public void deleteApartment(Long id, Jwt jwt) {
        var apartment = apartmentRepository.findById(id).orElseThrow(() -> new ApartmentNotFoundException(id));

        JwtUtils.requireOwner(jwt, apartment.getLandlordId());

        apartmentRepository.deleteById(id);
    }

    public List<Apartment> searchApartments(ApartmentFilter filter, Jwt jwt) {
        filter.setLandlordId(jwt.getSubject());

        return apartmentRepository.searchApartments(filter);
    }
}
