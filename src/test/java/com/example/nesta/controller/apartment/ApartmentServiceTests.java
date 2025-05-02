//package com.example.nesta.controller.apartment;
//
//import com.example.nesta.dto.ApartmentFilter;
//import com.example.nesta.exception.apartment.ApartmentNotFoundException;
//import com.example.nesta.model.Apartment;
//import com.example.nesta.repository.apartment.ApartmentRepository;
//import com.example.nesta.service.apartment.ApartmentService;
//import com.example.nesta.fixtures.ApartmentFixtures;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ApartmentServiceTests {
//    @Mock
//    private ApartmentRepository apartmentRepository;
//
//    @InjectMocks
//    private ApartmentService apartmentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createApartment_shouldSaveAndReturnApartment() {
//        // Arrange
//        Apartment apartment = ApartmentFixtures.validApartment();
//        when(apartmentRepository.save(apartment)).thenReturn(apartment);
//
//        // Act
//        Apartment result = apartmentService.createApartment(apartment);
//
//        // Assert
//        assertEquals(apartment, result);
//        verify(apartmentRepository).save(apartment);
//    }
//
//    @Test
//    void getApartmentById_shouldReturnApartmentIfExists() {
//        // Arrange
//        Apartment apartment = ApartmentFixtures.validApartment();
//        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(apartment));
//
//        // Act
//        Optional<Apartment> result = apartmentService.getApartmentById(1L);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(apartment, result.get());
//    }
//
//    @Test
//    void getApartmentById_shouldReturnEmptyIfNotExists() {
//        // Arrange
//        when(apartmentRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Act
//        Optional<Apartment> result = apartmentService.getApartmentById(999L);
//
//        // Assert
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getAllApartments_shouldReturnList() {
//        // Arrange
//        List<Apartment> apartments = List.of(ApartmentFixtures.validApartment());
//        when(apartmentRepository.findAll()).thenReturn(apartments);
//
//        // Act
//        List<Apartment> result = apartmentService.getAllApartments();
//
//        // Assert
//        assertEquals(apartments, result);
//    }
//
//    @Test
//    void updateApartment_shouldUpdateAndReturnApartmentIfExists() {
//        // Arrange
//        Apartment existing = ApartmentFixtures.validApartment();
//        existing.setId(1L);
//
//        Apartment updated = ApartmentFixtures.validApartment();
//        updated.setNumberOfRooms(5); // simulate update
//
//        when(apartmentRepository.findById(1L)).thenReturn(Optional.of(existing));
//        when(apartmentRepository.save(updated)).thenReturn(updated);
//
//        // Act
//        Apartment result = apartmentService.updateApartment(1L, updated);
//
//        // Assert
//        assertEquals(5, result.getNumberOfRooms());
//        verify(apartmentRepository).save(updated);
//    }
//
//    @Test
//    void updateApartment_shouldThrowIfApartmentNotFound() {
//        // Arrange
//        Apartment updated = ApartmentFixtures.validApartment();
//        when(apartmentRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Assert
//        assertThrows(ApartmentNotFoundException.class,
//                () -> apartmentService.updateApartment(999L, updated));
//    }
//
//    @Test
//    void deleteApartment_shouldDeleteIfExists() {
//        // Arrange
//        when(apartmentRepository.existsById(1L)).thenReturn(true);
//
//        // Act
//        apartmentService.deleteApartment(1L);
//
//        // Assert
//        verify(apartmentRepository).deleteById(1L);
//    }
//
//    @Test
//    void deleteApartment_shouldThrowIfNotExists() {
//        // Arrange
//        when(apartmentRepository.existsById(999L)).thenReturn(false);
//
//        // Assert
//        assertThrows(ApartmentNotFoundException.class,
//                () -> apartmentService.deleteApartment(999L));
//    }
//
//    @Test
//    void searchApartments_shouldCallRepositoryAndReturnList() {
//        // Arrange
//        ApartmentFilter filter = new ApartmentFilter();
//        List<Apartment> expected = List.of(ApartmentFixtures.validApartment());
//
//        when(apartmentRepository.searchApartments(filter)).thenReturn(expected);
//
//        // Act
//        List<Apartment> result = apartmentService.searchApartments(filter);
//
//        // Assert
//        assertEquals(expected, result);
//        verify(apartmentRepository).searchApartments(filter);
//    }
//}
