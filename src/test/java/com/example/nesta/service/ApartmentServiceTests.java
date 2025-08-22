package com.example.nesta.service;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.exception.apartment.ApartmentNotFoundException;
import com.example.nesta.model.Apartment;
import com.example.nesta.repository.apartment.ApartmentRepository;
import com.example.nesta.service.apartment.ApartmentService;
import com.example.nesta.fixtures.ApartmentFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApartmentServiceTests {

    @Mock
    private ApartmentRepository apartmentRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createApartment_shouldSaveAndReturnApartmentForJwtSubject() {
        // given
        Apartment apartment = ApartmentFixtures.apartment();
        String landlordId = "landlord-123";
        var jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(landlordId);
        when(apartmentRepository.save(apartment)).thenReturn(apartment);

        // when
        Apartment result = apartmentService.createApartment(apartment, jwt);

        // then
        assertEquals(apartment, result);
        verify(apartmentRepository).save(apartment);
    }

    @Test
    void getAllApartmentsByLandlordId_shouldReturnListForJwtSubject() {
        // given
        String landlordId = "landlord-123";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(landlordId);

        List<Apartment> expected = List.of(ApartmentFixtures.apartment());
        when(apartmentRepository.getAllApartmentsByLandlordId(landlordId)).thenReturn(expected);

        // when
        List<Apartment> result = apartmentService.getAllApartmentsByLandlordId(jwt);

        // then
        assertEquals(expected, result);
        verify(jwt).getSubject();
        verify(apartmentRepository).getAllApartmentsByLandlordId(landlordId);
        verifyNoMoreInteractions(apartmentRepository, jwt);
    }

    @Test
    void getApartmentById_shouldReturnApartmentIfExists() {
        // given
        Apartment apartment = ApartmentFixtures.apartment();
        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(apartment));

        // when
        Optional<Apartment> result = apartmentService.getApartmentById(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(apartment, result.get());
    }

    @Test
    void getApartmentById_shouldReturnEmptyIfNotExists() {
        // given
        when(apartmentRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Apartment> result = apartmentService.getApartmentById(999L);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void updateApartment_shouldUpdateAndReturnApartmentIfExists() {
        // given
        Apartment existing = ApartmentFixtures.apartment();
        existing.setId(1L);

        Apartment updated = ApartmentFixtures.apartment();
        updated.setNumberOfRooms(5); // simulate update

        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(apartmentRepository.save(updated)).thenReturn(updated);

        // when
        Apartment result = apartmentService.updateApartment(1L, updated);

        // then
        assertEquals(5, result.getNumberOfRooms());
        verify(apartmentRepository).save(updated);
    }

    @Test
    void updateApartment_shouldThrowIfApartmentNotFound() {
        // given
        Apartment updated = ApartmentFixtures.apartment();
        when(apartmentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ApartmentNotFoundException.class,
                () -> apartmentService.updateApartment(999L, updated));
    }

    @Test
    void deleteApartment_shouldDeleteIfExists() {
        // given
        when(apartmentRepository.existsById(1L)).thenReturn(true);

        // when
        apartmentService.deleteApartment(1L);

        // then
        verify(apartmentRepository).deleteById(1L);
    }

    @Test
    void deleteApartment_shouldThrowIfNotExists() {
        // given
        when(apartmentRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThrows(ApartmentNotFoundException.class,
                () -> apartmentService.deleteApartment(999L));
    }

    @Test
    void searchApartments_shouldCallRepositoryAndReturnList() {
        // given
        ApartmentFilter filter = new ApartmentFilter();
        List<Apartment> expected = List.of(ApartmentFixtures.apartment());
        when(apartmentRepository.searchApartments(filter)).thenReturn(expected);

        // when
        List<Apartment> result = apartmentService.searchApartments(filter);

        // then
        assertEquals(expected, result);
        verify(apartmentRepository).searchApartments(filter);
    }
}
