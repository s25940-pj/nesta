package com.example.nesta.exception.apartment;

import com.example.nesta.exception.common.ResourceAlreadyExistsException;

public class ApartmentAlreadyExistsForAddressException extends ResourceAlreadyExistsException {
    public ApartmentAlreadyExistsForAddressException(String message) {
        super(message);
    }
}
