package com.example.nesta.service;

import com.example.nesta.dto.PaymentRequest;
import com.example.nesta.model.Payment;
import com.example.nesta.model.RentalInvoice;
import com.example.nesta.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalInvoiceRepository rentalInvoiceRepository;

    public Payment createPayment(PaymentRequest request) {
        RentalInvoice invoice = rentalInvoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        Payment payment = Payment.builder()
                .rentalInvoice(invoice)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status("PENDING")
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }
}
