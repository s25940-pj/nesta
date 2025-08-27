package com.example.nesta.controller.rentalinvoice;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceListItemDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceQuery;
import com.example.nesta.service.rentalinvoice.RentalInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class RentalInvoiceController {
    private final RentalInvoiceService rentalInvoiceService;

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PostMapping("/create")
    public RentalInvoiceDto create(@RequestBody RentalInvoiceDto req, @AuthenticationPrincipal Jwt jwt) {
        return rentalInvoiceService.create(req, jwt.getSubject());
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PutMapping("{id}")
    public RentalInvoiceDto update(
            @PathVariable UUID id,
            @RequestBody RentalInvoiceDto dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return rentalInvoiceService.update(id, dto, jwt.getSubject());
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        rentalInvoiceService.delete(id, jwt.getSubject());
    }

    @GetMapping
    public Page<RentalInvoiceListItemDto> listMine(@ModelAttribute RentalInvoiceQuery q, @AuthenticationPrincipal Jwt jwt) {
        return rentalInvoiceService.listMine(q, jwt.getSubject());
    }
}