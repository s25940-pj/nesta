package com.example.nesta.service.apartment;

import com.example.nesta.dto.apartment.ApartmentFilter;
import com.example.nesta.model.Apartment;
import com.example.nesta.repository.apartment.ApartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    
    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }
    
    public Apartment updateApartment(Long id, Apartment updatedApartment) {
        return apartmentRepository.findById(id)
                .map(existing -> {
                    updatedApartment.setId(id); // zapewniamy, że ID się nie zmienia
                    return apartmentRepository.save(updatedApartment);
                })
                .orElseThrow(() -> new RuntimeException("Apartment not found with id: " + id));
    }

    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }

    public List<Apartment> searchApartments(ApartmentFilter filter) {
        return apartmentRepository.searchApartments(filter);
    }

}
