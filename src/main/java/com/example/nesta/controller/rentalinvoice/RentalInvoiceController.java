package com.example.nesta.controller.rentalinvoice;

import com.example.nesta.model.RentalInvoice;
import com.example.nesta.repository.rentalinvoice.RentalInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class RentalInvoiceController {
    private final RentalInvoiceRepository invoiceRepo;

    @PostMapping("/create")
    public RentalInvoice create(@RequestBody RentalInvoice invoice) {
        return invoiceRepo.save(invoice);
    }
}