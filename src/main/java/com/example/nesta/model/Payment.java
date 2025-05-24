package com.example.nesta.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private RentalInvoice rentalInvoice;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private String method; // np. "PRZELEW", "KARTA", "P24"

    private String status; // np. "PENDING", "COMPLETED", "FAILED"
} 