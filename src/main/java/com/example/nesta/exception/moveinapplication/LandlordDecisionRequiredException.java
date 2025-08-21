package com.example.nesta.exception.moveinapplication;

public class LandlordDecisionRequiredException extends RuntimeException {
  // TODO: dodać do GlobalExceptionHandler
    public LandlordDecisionRequiredException() {
        super("Landlord must approve the application before this action can be performed.");
    }
}
