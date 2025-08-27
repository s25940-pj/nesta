package com.example.nesta.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@Table(name = "rental_invoice", indexes = {
        @Index(name = "idx_invoice_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_invoice_paid", columnList = "paid")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalInvoice {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_offer_id", nullable = false)
    private RentalOffer rentalOffer;

    @Column(name = "issuer_user_id", nullable = false)
    private String issuerId;     // landlord

    @Column(name = "receiver_user_id")
    private String receiverId;     // rentier

    @Column(name = "number", length = 64)
    private String number;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PLN";

    @Column(name = "paid", nullable = false)
    private boolean paid = false;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "payment_id")
    private UUID paymentId;

    /** Audit. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
