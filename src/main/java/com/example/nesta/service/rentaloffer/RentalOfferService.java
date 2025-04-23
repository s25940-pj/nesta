package com.example.nesta.service.rentaloffer;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.exception.RentalOfferNotFoundException;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.repository.rentaloffer.RentalOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalOfferService {
    private final RentalOfferRepository rentalOfferRepository;


    public RentalOfferService(RentalOfferRepository rentalOfferRepository) {
        this.rentalOfferRepository = rentalOfferRepository;
    }

    public RentalOffer createRentalOffer(RentalOffer rentalOffer) {
        return rentalOfferRepository.save(rentalOffer);
    }

    public Optional<RentalOffer> getRentalOfferById(Long id) { return rentalOfferRepository.findById(id); }

    public List<RentalOffer> getAllRentalOffers() { return rentalOfferRepository.findAll(); }

    public RentalOffer updateRentalOffer(Long id, RentalOffer updatedRentalOffer) {
        return rentalOfferRepository.findById(id)
                .map(existing -> {
                    updatedRentalOffer.setId(id);
                    return rentalOfferRepository.save(updatedRentalOffer);
                })
                .orElseThrow(() -> new RentalOfferNotFoundException(id));
    }

    public void deleteRentalOffer(Long id) {
        if (!rentalOfferRepository.existsById(id)) {
            throw new RentalOfferNotFoundException(id);
        }
        rentalOfferRepository.deleteById(id);
    }

    public List<RentalOffer> searchRentalOffers(RentalOfferFilter filter) {
        return rentalOfferRepository.searchRentalOffers(filter);
    }
}
