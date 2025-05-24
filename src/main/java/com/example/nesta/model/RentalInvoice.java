package com.example.nesta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RentalInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rental_offer_id", nullable = false)
    private RentalOffer rentalOffer;

    @Column(name = "rentier_id")
    private String rentierId;

    @NotNull
    private double amount;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date issueDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    private boolean isPaid = false;
}
