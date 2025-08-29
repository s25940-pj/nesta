package com.example.nesta.exception.moveinapplication;

public class LandlordNotApprovedException extends RuntimeException {
    public LandlordNotApprovedException() {
        super("Landlord has not yet approved application");
    }
}
