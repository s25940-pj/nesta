package com.example.nesta.controller.rentalOffer;

import com.example.nesta.controller.rentaloffer.RentalOfferController;
import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.*;
import com.example.nesta.model.enums.*;
import com.example.nesta.service.rentaloffer.RentalOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalOfferControllerTest {

    @Mock
    private RentalOfferService rentalOfferService;

    @InjectMocks
    private RentalOfferController rentalOfferController;

    private RentalOffer sampleOffer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Apartment apartment = new Apartment(); // zakładamy, że istnieje pusty konstruktor

        sampleOffer = new RentalOffer();
        sampleOffer.setId(1L);
        sampleOffer.setApartment(apartment);
        sampleOffer.setMonthlyRent(new BigDecimal("2000"));
        sampleOffer.setDeposit(new BigDecimal("2000"));
        sampleOffer.setUtilitiesCost(new BigDecimal("300"));
        sampleOffer.setUtilitiesIncluded(false);
        sampleOffer.setAvailableFrom(LocalDate.of(2025, 9, 1));
        sampleOffer.setAvailableUntil(LocalDate.of(2026, 9, 1));
        sampleOffer.setShortTermRental(false);
        sampleOffer.setFurnishingStatus(FurnishingStatus.FURNISHED);
        sampleOffer.setPreferredEmploymentStatus(EmploymentStatus.EMPLOYED);
        sampleOffer.setSmokingPolicy(SmokingPolicy.NO);
        sampleOffer.setPetPolicy(PetPolicy.NO);
        sampleOffer.setAccessibleForDisabled(true);
    }

    @Test
    void testCreateRentalOffer() {
        when(rentalOfferService.createRentalOffer(any())).thenReturn(sampleOffer);

        ResponseEntity<RentalOffer> response = rentalOfferController.createRentalOffer(sampleOffer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleOffer, response.getBody());
        verify(rentalOfferService).createRentalOffer(sampleOffer);
    }

    @Test
    void testGetRentalOfferById_Found() {
        when(rentalOfferService.getRentalOfferById(1L)).thenReturn(Optional.of(sampleOffer));

        ResponseEntity<RentalOffer> response = rentalOfferController.getRentalOfferById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleOffer, response.getBody());
    }

    @Test
    void testGetRentalOfferById_NotFound() {
        when(rentalOfferService.getRentalOfferById(999L)).thenReturn(Optional.empty());

        ResponseEntity<RentalOffer> response = rentalOfferController.getRentalOfferById(999L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllRentalOffers() {
        List<RentalOffer> offers = List.of(sampleOffer);
        when(rentalOfferService.getAllRentalOffers()).thenReturn(offers);

        ResponseEntity<List<RentalOffer>> response = rentalOfferController.getAllRentalOffers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(offers, response.getBody());
    }

    @Test
    void testUpdateRentalOffer() {
        when(rentalOfferService.updateRentalOffer(eq(1L), any())).thenReturn(sampleOffer);

        ResponseEntity<RentalOffer> response = rentalOfferController.updateRentalOffer(1L, sampleOffer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sampleOffer, response.getBody());
    }

    @Test
    void testDeleteRentalOffer() {
        doNothing().when(rentalOfferService).deleteRentalOffer(1L);

        ResponseEntity<Void> response = rentalOfferController.deleteRentalOffer(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(rentalOfferService).deleteRentalOffer(1L);
    }

    @Test
    void testSearchRentalOffers() {
        RentalOfferFilter filter = new RentalOfferFilter();
        Map<String, String> params = Map.of("monthlyRent", "2000");

        when(rentalOfferService.searchRentalOffers(filter)).thenReturn(List.of(sampleOffer));

        ResponseEntity<List<RentalOffer>> response = rentalOfferController.searchRentalOffers(filter, params);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}
