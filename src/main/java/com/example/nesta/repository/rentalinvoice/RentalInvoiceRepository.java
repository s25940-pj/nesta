package com.example.nesta.repository.rentalinvoice;

import com.example.nesta.model.RentalInvoice;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalInvoiceRepository extends JpaRepository<RentalInvoice, UUID>, RentalInvoiceRepositoryQuery {
    @Query("""
        select i
        from RentalInvoice i
        where i.id = :id and (i.issuerId = :userId or i.receiverId = :userId)
    """)
    Optional<RentalInvoice> findByIdAndUserId(UUID id, String userId);
}

