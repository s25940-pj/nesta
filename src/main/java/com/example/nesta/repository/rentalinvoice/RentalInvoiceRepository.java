package com.example.nesta.repository.rentalinvoice;

import com.example.nesta.model.RentalInvoice;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalInvoiceRepository extends JpaRepository<RentalInvoice, UUID> {

    Optional<RentalInvoice> findByIdAndUserId(UUID id, String userId);

    Page<RentalInvoice> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<RentalInvoice> findAllByUserIdAndPaidFalseOrderByCreatedAtDesc(String userId, Pageable pageable);
}

