package com.example.nesta.exception;

public class ApartmentNotFoundException extends RuntimeException {
  public ApartmentNotFoundException(Long id) {
    super("Apartment with id " + id + " not found");
  }
}
