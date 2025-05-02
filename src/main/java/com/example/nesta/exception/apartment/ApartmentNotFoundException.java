package com.example.nesta.exception.apartment;

import com.example.nesta.exception.common.ResourceNotFoundException;

public class ApartmentNotFoundException extends ResourceNotFoundException {
  public ApartmentNotFoundException(Long id) {
    super("Apartment with id " + id + " not found");
  }
}
