package com.example.nesta.service.apartment;

import com.example.nesta.model.Apartment;
import com.example.nesta.repository.apartment.ApartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService implements IApartmentService {
    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    @Override
    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    @Override
    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    @Override
    public Apartment updateApartment(Long id, Apartment updatedApartment) {
        return apartmentRepository.findById(id)
                .map(existing -> {
                    updatedApartment.setId(id); // zapewniamy, że ID się nie zmienia
                    return apartmentRepository.save(updatedApartment);
                })
                .orElseThrow(() -> new RuntimeException("Apartment not found with id: " + id));
    }

    @Override
    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }
}
