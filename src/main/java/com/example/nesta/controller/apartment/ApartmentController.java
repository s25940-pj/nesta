package com.example.nesta.controller.apartment;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.model.Apartment;
import com.example.nesta.service.apartment.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {
    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PostMapping
    public ResponseEntity<Apartment> createApartment(@RequestBody @Valid Apartment apartment, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(apartmentService.createApartment(apartment, jwt));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @GetMapping("/{id}")
    public ResponseEntity<Apartment> getApartmentById(@PathVariable Long id) {
        return apartmentService.getApartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @GetMapping
    public ResponseEntity<List<Apartment>> getAllApartments(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(apartmentService.getAllApartments(jwt));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PutMapping("/{id}")
    public ResponseEntity<Apartment> updateApartment(@PathVariable Long id, @RequestBody @Valid Apartment apartment) {
        return ResponseEntity.ok(apartmentService.updateApartment(id, apartment));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Apartment>> searchApartments(@ModelAttribute ApartmentFilter filter, @RequestParam Map<String, String> allParams) {
        Set<String> allowedParams = Arrays.stream(ApartmentFilter.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (String param : allParams.keySet()) {
            if (!allowedParams.contains(param)) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(apartmentService.searchApartments(filter));
    }
}
