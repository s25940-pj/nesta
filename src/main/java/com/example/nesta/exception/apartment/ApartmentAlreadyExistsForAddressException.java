package com.example.nesta.exception.apartment;

public class ApartmentAlreadyExistsForAddressException extends RuntimeException {
    public ApartmentAlreadyExistsForAddressException(String message) {
        super(message);
    }
}
