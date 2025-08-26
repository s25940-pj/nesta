package com.example.nesta.exception;

import com.example.nesta.exception.common.ResourceNotFoundException;

public class ApartmentImageNotFoundException extends ResourceNotFoundException {
    public ApartmentImageNotFoundException(Long id) {
        super("Apartment image not found with id " + id);
    }
}
