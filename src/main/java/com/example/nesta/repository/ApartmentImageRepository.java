package com.example.nesta.repository;

import com.example.nesta.model.ApartmentImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentImageRepository extends JpaRepository<ApartmentImage, Long> {
    int countByApartmentId(long apartmentId);
}
