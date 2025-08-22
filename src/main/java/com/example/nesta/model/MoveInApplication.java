package com.example.nesta.model;

import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MoveInApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_offer_id", nullable = false)
    private RentalOffer rentalOffer;

    @Column(name = "rentier_id")
    private String rentierId;

    @Column(name = "viewing_datetime")
    @NotNull
    private LocalDateTime viewingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "landlord_status", nullable = false)
    private MoveInApplicationStatus landlordStatus = MoveInApplicationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "rentier_status", nullable = false)
    private MoveInApplicationStatus rentierStatus = MoveInApplicationStatus.PENDING;

    @Column(length = 300)
    private String landlordDecisionReason;

    @Column(length = 300)
    private String rentierDecisionReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime landlordDecidedAt;
    private LocalDateTime rentierDecidedAt;
}
