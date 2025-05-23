package com.example.nesta.controller.rentaloffer;

import com.example.nesta.controller.AbstractSearchController;
import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.service.rentaloffer.RentalOfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<RentalOffer> createRentalOffer(@RequestBody @Valid RentalOffer rentalOffer) {
        return ResponseEntity.ok(rentalOfferService.createRentalOffer(rentalOffer));
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

    @PutMapping("/{id}")
    public ResponseEntity<RentalOffer> updateRentalOffer(@PathVariable Long id, @RequestBody @Valid RentalOffer rentalOffer) {
        return ResponseEntity.ok(rentalOfferService.updateRentalOffer(id, rentalOffer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalOffer(@PathVariable Long id) {
        rentalOfferService.deleteRentalOffer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RentalOffer>> searchRentalOffers(@ModelAttribute RentalOfferFilter filter, @RequestParam Map<String, String> allParams) {
        validateRequestParams(allParams, allowedQueryParams);

        return ResponseEntity.ok(rentalOfferService.searchRentalOffers(filter));
    }
}
