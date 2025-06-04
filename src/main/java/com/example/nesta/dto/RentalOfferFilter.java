package com.example.nesta.dto;

import com.example.nesta.model.enums.EmploymentStatus;
import com.example.nesta.model.enums.FurnishingStatus;
import com.example.nesta.model.enums.PetPolicy;
import com.example.nesta.model.enums.SmokingPolicy;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentalOfferFilter {
    private String landlordId;
    private ApartmentFilter apartment;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    private BigDecimal utilitiesCost;
    private Boolean utilitiesIncluded;
    private LocalDate availableFrom;
    private LocalDate availableUntil;
    private Boolean shortTermRental;
    private FurnishingStatus furnishingStatus;
    private EmploymentStatus preferredEmploymentStatus;
    private SmokingPolicy smokingPolicy;
    private PetPolicy petPolicy;
    private Boolean accessibleForDisabled;
}
