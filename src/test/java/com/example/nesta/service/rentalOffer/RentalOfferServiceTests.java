package com.example.nesta.service.rentaloffer;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.exception.rentaloffer.RentalOfferNotFoundException;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.repository.rentaloffer.RentalOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RentalOfferServiceTests {

    @Mock
    private RentalOfferRepository rentalOfferRepository;

    @InjectMocks
    private RentalOfferService rentalOfferService;

    private RentalOffer sampleOffer;

    @BeforeEach
    void setUp() {
        sampleOffer = new RentalOffer();
        sampleOffer.setId(1L);
        sampleOffer.setMonthlyRent(BigDecimal.valueOf(1200));
        sampleOffer.setDeposit(BigDecimal.valueOf(1000));
        sampleOffer.setAvailableFrom(LocalDate.now());
        sampleOffer.setAvailableUntil(LocalDate.now().plusMonths(12));
        sampleOffer.setUtilitiesIncluded(true);
    }

    @Test
    void createRentalOffer_shouldSaveOffer() {
        when(rentalOfferRepository.save(sampleOffer)).thenReturn(sampleOffer);

        RentalOffer saved = rentalOfferService.createRentalOffer(sampleOffer);

        assertEquals(sampleOffer, saved);
        verify(rentalOfferRepository).save(sampleOffer);
    }

    @Test
    void getRentalOfferById_whenFound_shouldReturnOffer() {
        when(rentalOfferRepository.findById(1L)).thenReturn(Optional.of(sampleOffer));

        Optional<RentalOffer> result = rentalOfferService.getRentalOfferById(1L);

        assertTrue(result.isPresent());
        assertEquals(sampleOffer, result.get());
    }

    @Test
    void getRentalOfferById_whenNotFound_shouldReturnEmptyOptional() {
        when(rentalOfferRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<RentalOffer> result = rentalOfferService.getRentalOfferById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRentalOffers_shouldReturnList() {
        List<RentalOffer> offers = List.of(sampleOffer);
        when(rentalOfferRepository.findAll()).thenReturn(offers);

        List<RentalOffer> result = rentalOfferService.getAllRentalOffers();

        assertEquals(1, result.size());
        assertEquals(sampleOffer, result.get(0));
    }

    @Test
    void updateRentalOffer_whenFound_shouldUpdate() {
        RentalOffer updated = new RentalOffer();
        updated.setMonthlyRent(BigDecimal.valueOf(1500));
        updated.setDeposit(BigDecimal.valueOf(1000));
        updated.setAvailableFrom(LocalDate.now());
        updated.setAvailableUntil(LocalDate.now().plusMonths(6));
        updated.setUtilitiesIncluded(false);

        when(rentalOfferRepository.findById(1L)).thenReturn(Optional.of(sampleOffer));
        when(rentalOfferRepository.save(updated)).thenReturn(updated);

        RentalOffer result = rentalOfferService.updateRentalOffer(1L, updated);

        assertEquals(updated.getMonthlyRent(), result.getMonthlyRent());
        verify(rentalOfferRepository).save(updated);
    }

    @Test
    void updateRentalOffer_whenNotFound_shouldThrow() {
        when(rentalOfferRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RentalOfferNotFoundException.class,
                () -> rentalOfferService.updateRentalOffer(99L, sampleOffer));
    }

    @Test
    void deleteRentalOffer_whenExists_shouldDelete() {
        when(rentalOfferRepository.existsById(1L)).thenReturn(true);

        rentalOfferService.deleteRentalOffer(1L);

        verify(rentalOfferRepository).deleteById(1L);
    }

    @Test
    void deleteRentalOffer_whenNotExists_shouldThrow() {
        when(rentalOfferRepository.existsById(99L)).thenReturn(false);

        assertThrows(RentalOfferNotFoundException.class,
                () -> rentalOfferService.deleteRentalOffer(99L));
    }

    @Test
    void searchRentalOffers_shouldDelegateToRepository() {
        RentalOfferFilter filter = new RentalOfferFilter();
        List<RentalOffer> offers = List.of(sampleOffer);
        when(rentalOfferRepository.searchRentalOffers(filter)).thenReturn(offers);

        List<RentalOffer> result = rentalOfferService.searchRentalOffers(filter);

        assertEquals(offers, result);
    }
}