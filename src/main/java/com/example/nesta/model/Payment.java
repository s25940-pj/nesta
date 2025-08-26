package com.example.nesta.model;

import com.example.nesta.model.enums.PaymentStatus;
import com.example.nesta.model.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payment",
        indexes = {
                @Index(name = "idx_payment_session_id", columnList = "session_id", unique = true),
                @Index(name = "idx_payment_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_payment_invoice", columnList = "invoice_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "PLN";

    @Column(name = "method", nullable = false, length = 32)
    private String method = "P24";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 16)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "session_id", nullable = false, unique = true, length = 128)
    private String sessionId;

    @Column(name = "p24_order_id")
    private Long p24OrderId;

    @Column(name = "p24_token", length = 256)
    private String p24Token;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Lob
    @Column(name = "raw_notification")
    private String rawNotification;

    /** Audit. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}