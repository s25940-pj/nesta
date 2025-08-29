package com.example.nesta.service.rentaloffer;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.exception.apartment.ApartmentNotFoundException;
import com.example.nesta.exception.rentaloffer.RentalOfferAlreadyExists;
import com.example.nesta.exception.rentaloffer.RentalOfferNotFoundException;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.repository.rentaloffer.RentalOfferRepository;
import com.example.nesta.utils.JwtUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalOfferService {
    private final RentalOfferRepository rentalOfferRepository;


    public RentalOfferService(RentalOfferRepository rentalOfferRepository) {
        this.rentalOfferRepository = rentalOfferRepository;
    }

    public RentalOffer createRentalOffer(RentalOffer rentalOffer, Jwt jwt) {
        var apartmentId = rentalOffer.getApartment().getId();
        var rentalOfferAlreadyExists = rentalOfferRepository.findByApartmentId(apartmentId).isPresent();

        if  (rentalOfferAlreadyExists) {
            throw new RentalOfferAlreadyExists(apartmentId);
        }

        String landlordId = jwt.getSubject();
        rentalOffer.setLandlordId(landlordId);

        try {
            return rentalOfferRepository.save(rentalOffer);
        }
        catch (DataIntegrityViolationException e) {
            // Thrown when the referenced apartment doesn't exist
            throw new ApartmentNotFoundException(rentalOffer.getApartment().getId());
        }
    }

    public Optional<RentalOffer> getRentalOfferById(Long id) { return rentalOfferRepository.findById(id); }

    public List<RentalOffer> getAllRentalOffers() { return rentalOfferRepository.findAll(); }

    public RentalOffer updateRentalOffer(Long id, RentalOffer updatedRentalOffer, Jwt jwt, boolean requireOwnerValidation) {
        try {
            return rentalOfferRepository.findById(id)
                    .map(existing -> {
                        if (requireOwnerValidation) JwtUtils.requireOwner(jwt, existing.getLandlordId());

                        updatedRentalOffer.setId(existing.getId());
                        updatedRentalOffer.setLandlordId(existing.getLandlordId());
                        return rentalOfferRepository.save(updatedRentalOffer);
                    })
                    .orElseThrow(() -> new RentalOfferNotFoundException(id));
        }
        catch (JpaObjectRetrievalFailureException e) {
            // Thrown when the referenced apartment doesn't exist
            throw new ApartmentNotFoundException(updatedRentalOffer.getApartment().getId());
        }
    }

    public void deleteRentalOffer(Long id, Jwt jwt) {
        var rentalOffer = rentalOfferRepository.findById(id).orElseThrow(() -> new RentalOfferNotFoundException(id));

        JwtUtils.requireOwner(jwt, rentalOffer.getLandlordId());

        rentalOfferRepository.deleteById(id);
    }

    public List<RentalOffer> searchRentalOffers(RentalOfferFilter filter) {
        return rentalOfferRepository.searchRentalOffers(filter);
    }
}
