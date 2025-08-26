package com.example.nesta.controller.apartment;

import com.example.nesta.controller.AbstractSearchController;
import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.dto.ApartmentImageDto;
import com.example.nesta.model.Apartment;
import com.example.nesta.service.ApartmentImageService;
import com.example.nesta.service.apartment.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController extends AbstractSearchController {
    private final ApartmentService apartmentService;
    private final ApartmentImageService apartmentImageService;

    private final static Set<String> ALLOWED_QUERY_PARAMS =
            Arrays.stream(ApartmentFilter.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toSet());

    public ApartmentController(ApartmentService apartmentService, ApartmentImageService apartmentImageService) {
        super(ALLOWED_QUERY_PARAMS);
        this.apartmentService = apartmentService;
        this.apartmentImageService = apartmentImageService;
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
    public ResponseEntity<List<Apartment>> getAllApartmentsByLandlordId(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(apartmentService.getAllApartmentsByLandlordId(jwt));
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

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @GetMapping("/search")
    public ResponseEntity<List<Apartment>> searchApartments(@ModelAttribute ApartmentFilter filter, @RequestParam Map<String, String> allParams) {
        validateRequestParams(allParams, allowedQueryParams);

        return ResponseEntity.ok(apartmentService.searchApartments(filter));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @PostMapping(path = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApartmentImageDto> uploadImage(
            @PathVariable long id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(apartmentImageService.uploadSingle(id, file, jwt));
    }

    @PreAuthorize("hasRole(T(com.example.nesta.model.enums.UserRole).LANDLORD)")
    @DeleteMapping("/{apartmentId}/images/{imageId}")
    public ResponseEntity<Void> deleteApartmentImage(@PathVariable Long apartmentId, @PathVariable Long imageId, @AuthenticationPrincipal Jwt jwt) {
        apartmentImageService.delete(apartmentId, imageId, jwt);
        return ResponseEntity.noContent().build();
    }
}
