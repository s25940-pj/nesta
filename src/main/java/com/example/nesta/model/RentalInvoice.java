package com.example.nesta.model;

import jakarta.persistence.*;
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
    @JoinColumn(name = "rental_offer_id")
    private RentalOffer rentalOffer;

    private double amount;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    private boolean isPaid;
}
