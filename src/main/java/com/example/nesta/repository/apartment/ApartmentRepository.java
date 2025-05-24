package com.example.nesta.repository.apartment;

import com.example.nesta.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long>, ApartmentQueryRepository {
    Optional<Apartment> findByStreetNameAndBuildingNumberAndApartmentNumberAndCityAndPostalCodeAndCountry(
            String streetName,
            String buildingNumber,
            String apartmentNumber,
            String city,
            String postalCode,
            String country
    );

    List<Apartment> getAllApartmentsByLandlordId(String userId);
}
