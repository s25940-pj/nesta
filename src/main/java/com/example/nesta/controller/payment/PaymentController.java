package com.example.nesta.controller.payment;

import com.example.nesta.dto.PaymentRequest;
import com.example.nesta.model.Payment;
import com.example.nesta.model.RentalInvoice;
import com.example.nesta.repository.invoice.RentalInvoiceRepository;
import com.example.nesta.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final RentalInvoiceRepository rentalInvoiceRepository;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        RentalInvoice invoice = rentalInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        Payment payment = Payment.builder()
                .rentalInvoice(invoice)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(paymentRepository.save(payment));
    }
}