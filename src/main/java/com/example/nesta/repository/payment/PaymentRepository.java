package com.example.nesta.repository.payment;

import java.util.UUID;

import com.example.nesta.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, PaymentRepositoryQuery {

    Payment findBySessionId(String sessionId);

    @Query("select p from Payment p where p.invoiceId = :invoiceId and p.status = 'PENDING'")
    Payment findPendingByInvoiceId(UUID invoiceId);
}
