package com.example.nesta.exception;

public class RentalOfferNotFoundException extends RuntimeException {
    public RentalOfferNotFoundException(Long id) {
        super("Rental offer with id " + id + " not found");
    }
}
