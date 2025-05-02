package com.example.nesta.exception.rentaloffer;

import com.example.nesta.exception.common.ResourceNotFoundException;

public class RentalOfferNotFoundException extends ResourceNotFoundException {
    public RentalOfferNotFoundException(Long id) {
        super("Rental offer with id " + id + " not found");
    }
}
