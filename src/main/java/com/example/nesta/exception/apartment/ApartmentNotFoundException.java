package com.example.nesta.exception.apartment;

public class ApartmentNotFoundException extends RuntimeException {
  public ApartmentNotFoundException(Long id) {
    super("Apartment not found with id: " + id);
  }
}
