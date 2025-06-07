package com.example.nesta.exception.rentaloffer;

import com.example.nesta.exception.common.ResourceAlreadyExistsException;

public class RentalOfferAlreadyExists extends ResourceAlreadyExistsException {
    public RentalOfferAlreadyExists(Long apartmentId) {
        super("Rental offer for apartment with id " + apartmentId + " already exists");
    }
}
