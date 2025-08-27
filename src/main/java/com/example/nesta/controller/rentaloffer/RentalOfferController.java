package com.example.nesta.controller.rentaloffer;

import com.example.nesta.controller.AbstractSearchController;
import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.service.rentaloffer.RentalOfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/rental-offers")
public class RentalOfferController extends AbstractSearchController {
    private final RentalOfferService rentalOfferService;

    private final static Set<String> ALLOWED_QUERY_PARAMS = Stream.concat(
            Arrays.stream(RentalOfferFilter.class.getDeclaredFields()).map(Field::getName),
            Arrays.stream(ApartmentFilter.class.getDeclaredFields()).map(field -> "apartment." + field.getName())
    ).collect(Collectors.toSet());

    public RentalOfferController(RentalOfferService rentalOfferService) {
        super(ALLOWED_QUERY_PARAMS);
        this.rentalOfferService = rentalOfferService;
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PostMapping
    public ResponseEntity<RentalOffer> createRentalOffer(@RequestBody @Valid RentalOffer rentalOffer, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(rentalOfferService.createRentalOffer(rentalOffer, jwt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalOffer> getRentalOfferById(@PathVariable Long id) {
        return rentalOfferService.getRentalOfferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RentalOffer>> getAllRentalOffers() {
        return ResponseEntity.ok(rentalOfferService.getAllRentalOffers());
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PutMapping("/{id}")
    public ResponseEntity<RentalOffer> updateRentalOffer(@PathVariable Long id, @RequestBody @Valid RentalOffer rentalOffer, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(rentalOfferService.updateRentalOffer(id, rentalOffer, jwt));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalOffer(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        rentalOfferService.deleteRentalOffer(id, jwt);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RentalOffer>> searchRentalOffers(@ModelAttribute RentalOfferFilter filter, @RequestParam Map<String, String> allParams) {
        validateRequestParams(allParams, allowedQueryParams);

        return ResponseEntity.ok(rentalOfferService.searchRentalOffers(filter));
    }
}
