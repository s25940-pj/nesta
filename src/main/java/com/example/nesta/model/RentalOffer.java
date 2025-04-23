package com.example.nesta.model;

import com.example.nesta.model.enums.EmploymentStatus;
import com.example.nesta.model.enums.FurnishingStatus;
import com.example.nesta.model.enums.PetPolicy;
import com.example.nesta.model.enums.SmokingPolicy;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RentalOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The apartment associated with this rental offer.
     */
    @OneToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    /**
     * Monthly rent amount (excluding additional utilities).
     */
    @NotNull
    private BigDecimal monthlyRent;

    /**
     * Required security deposit to start the rental.
     */
    @NotNull
    private BigDecimal deposit;

    /**
     * Estimated monthly cost of utilities such as electricity, gas, water, internet.
     */
    private BigDecimal utilitiesCost;

    /**
     * Whether utilities are included in the rent.
     * true → included in monthlyRent, false → paid separately.
     */
    @NotNull
    private boolean utilitiesIncluded;

    /**
     * The date from which the apartment is available for rent.
     */
    @NotNull
    private LocalDate availableFrom;

    /**
     * The date until which the apartment is available for rent.
     */
    @NotNull
    private LocalDate availableUntil;

    /**
     * Indicates if short-term rental is possible.
     */
    @NotNull
    private boolean shortTermRental;

    /**
     * Furnishing status of the apartment.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private FurnishingStatus furnishingStatus;

    /**
     * Preferred employment status of the tenant.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private EmploymentStatus preferredEmploymentStatus;

    /**
     * Smoking policy in the apartment.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private SmokingPolicy smokingPolicy;

    /**
     * Policy on keeping pets in the apartment.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private PetPolicy petPolicy;

    /**
     * Indicates whether the apartment is accessible for people with disabilities.
     */
    @NotNull
    private boolean accessibleForDisabled;
}
