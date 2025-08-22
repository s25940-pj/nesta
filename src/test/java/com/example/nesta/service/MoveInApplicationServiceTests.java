package com.example.nesta.service;

import com.example.nesta.dto.moveinapplication.DecisionRequest;
import com.example.nesta.dto.moveinapplication.RescheduleRequest;
import com.example.nesta.exception.common.InvalidReferenceException;
import com.example.nesta.exception.moveinapplication.*;
import com.example.nesta.fixtures.MoveInApplicationFixtures;
import com.example.nesta.model.MoveInApplication;
import com.example.nesta.model.enums.MoveInApplicationStatus;
import com.example.nesta.repository.moveinapplication.MoveInApplicationRepository;
import com.example.nesta.service.moveinapplication.MoveInApplicationService;
import com.example.nesta.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoveInApplicationServiceTests {
    @Mock
    private MoveInApplicationRepository repository;

    @InjectMocks
    private MoveInApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldThrowWhenViewingDateIsNotAvailable() {
        // given
        MoveInApplication moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        Jwt jwt = mock(Jwt.class);
        var rentalOfferId = moveInApplication.getRentalOffer().getId();

        when(repository.existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(rentalOfferId, moveInApplication.getViewingDateTime())).thenReturn(true);

        // when & then
        var ex = assertThrows(ViewingDateNotAvailableException.class, () -> service.create(moveInApplication, jwt));
        assertEquals("The selected viewing date is already reserved by another active move-in application", ex.getMessage());
    }

    @Test
    void create_shouldThrowWhenViewingDateIsNotInFuture() {
        // given
        MoveInApplication moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var now = LocalDateTime.of(2025, 1, 15, 10, 30);
        var viewingDateTimeInPast = now.minusDays(1);

        moveInApplication.setViewingDateTime(viewingDateTimeInPast);

        Jwt jwt = mock(Jwt.class);

        try (var mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(now);

            // when & then
            var ex = assertThrows(
                    ViewingDateNotAvailableException.class,
                    () -> service.create(moveInApplication, jwt)
            );
            assertEquals("Viewing date must be in the future", ex.getMessage());
        }
    }

    @Test
    void create_shouldThrowWhenActiveApplicationExists() {
        // given
        MoveInApplication moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var rentalOfferId = moveInApplication.getRentalOffer().getId();
        Jwt jwt = mock(Jwt.class);
        var rentierId = "rentier-1";

        when(jwt.getSubject()).thenReturn(rentierId);
        when(repository.existsRentierPendingByRentalOfferAndRentierId(rentalOfferId, rentierId)).thenReturn(true);

        // when & then
        var ex = assertThrows(ActiveApplicationAlreadyExistsException.class, () -> service.create(moveInApplication, jwt));
        assertEquals("You already have an active application for this offer", ex.getMessage());
    }

    @Test
    void create_shouldThrowInvalidReferenceWhenRentalOfferDoesNotExist() {
        // given
        MoveInApplication moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        Jwt jwt = mock(Jwt.class);
        var rentalOfferId = moveInApplication.getRentalOffer().getId();

        // Note: We stub existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(...)
        // because in the service flow this is the FIRST repository call executed
        // for the given rental offer. We want the DataIntegrityViolationException
        // to be thrown at this point, so the service translates it into
        // InvalidReferenceException.
        when(repository.existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(rentalOfferId, moveInApplication.getViewingDateTime())).thenThrow(
                new DataIntegrityViolationException(String.format("Key (rental_offer_id)=(%d) is not present in table \"rental_offer\"", moveInApplication.getRentalOffer().getId()))
        );

        // when & then
        var ex = assertThrows(InvalidReferenceException.class, () -> service.create(moveInApplication, jwt));
        assertEquals(String.format("Rental offer with id %d does not exist.", rentalOfferId), ex.getMessage());
    }

    @Test
    void create_shouldSaveNewApplication() {
        // given
        MoveInApplication moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        Jwt jwt = mock(Jwt.class);
        var rentalOfferId = moveInApplication.getRentalOffer().getId();

        when(jwt.getSubject()).thenReturn("rentier-1");
        when(repository.existsRentierPendingByRentalOfferAndRentierId(rentalOfferId, moveInApplication.getRentierId())).thenReturn(false);
        when(repository.existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(rentalOfferId, moveInApplication.getViewingDateTime())).thenReturn(false);
        when(repository.save(moveInApplication)).thenReturn(moveInApplication);

        // when
        MoveInApplication result = service.create(moveInApplication, jwt);

        // then
        assertEquals(moveInApplication, result);
        verify(repository).save(moveInApplication);
    }

    @Test
    void rescheduleViewing_ThrowsWhenMoveInApplicationDoesNotExist() {
        // given
        var moveInApplicationId = 1L;
        var updatedViewingDateTime = LocalDateTime.of(2025, 1, 1, 1, 1, 1);
        var rescheduleRequest = new RescheduleRequest(updatedViewingDateTime);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.empty());

        // when & then
        var ex =  assertThrows(MoveInApplicationNotFoundException.class, () -> service.rescheduleViewing(moveInApplicationId, rescheduleRequest));
        assertEquals(String.format("Move in application with id %d not found", moveInApplicationId), ex.getMessage());
    }

    @Test
    void rescheduleViewing_ThrowsWhenApplicationLandlordStatusIsPending() {
        // given
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var updatedViewingDateTime = moveInApplication.getViewingDateTime().plusDays(1);
        var rescheduleRequest = new RescheduleRequest(updatedViewingDateTime);

        moveInApplication.setId(1L);

        var moveInApplicationId = moveInApplication.getId();

        moveInApplication.setLandlordStatus(MoveInApplicationStatus.APPROVED);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        // when & then
        var ex =  assertThrows(ViewingRescheduleNotAllowedException.class, () -> service.rescheduleViewing(moveInApplicationId, rescheduleRequest));
        assertEquals("Cannot reschedule viewing because landlord has already made a decision", ex.getMessage());
    }

    @Test
    void rescheduleViewing_ThrowsWhenViewingDateIsUnchanged() {
        // given
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var rescheduleRequest = new RescheduleRequest(moveInApplication.getViewingDateTime());

        moveInApplication.setId(1L);

        var moveInApplicationId = moveInApplication.getId();

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        // when & then
        var ex =  assertThrows(ViewingDateUnchangedException.class, () -> service.rescheduleViewing(moveInApplicationId, rescheduleRequest));
        assertEquals("New viewing date must be different from the current one", ex.getMessage());
    }

    @Test
    void rescheduleViewing_ThrowsWhenViewingDateIsNotAvailable() {
        // given
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var updatedViewingDateTime = moveInApplication.getViewingDateTime().plusDays(1);
        var rescheduleRequest = new RescheduleRequest(updatedViewingDateTime);

        moveInApplication.setId(1L);

        var moveInApplicationId = moveInApplication.getId();

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));
        when(repository.existsByOfferAndViewingDateTimeAndRentierOrLandlordPending(moveInApplication.getRentalOffer().getId(), rescheduleRequest.updatedViewingDateTime())).thenReturn(true);

        // when & then
        var ex =  assertThrows(ViewingDateNotAvailableException.class, () -> service.rescheduleViewing(moveInApplicationId, rescheduleRequest));
        assertEquals("The selected viewing date is already reserved by another active move-in application", ex.getMessage());
    }

    @Test
    void rescheduleViewing_ThrowsWhenViewingDateIsNotInFuture() {
        // given
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var now = LocalDateTime.of(2025, 1, 15, 10, 30);
        var viewingDateTimeInPast = now.minusDays(1);
        var rescheduleRequest = new RescheduleRequest(viewingDateTimeInPast);
        var moveInApplicationId = moveInApplication.getId();

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        try (var mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(now);

            // when & then
            var ex = assertThrows(
                    ViewingDateNotAvailableException.class,
                    () -> service.rescheduleViewing(moveInApplicationId, rescheduleRequest)
            );
            assertEquals("Viewing date must be in the future", ex.getMessage());
        }
    }

    @Test
    void rescheduleViewing_shouldSaveApplicationWithRescheduledViewingDate() {
        //given
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();
        var updatedViewingDateTime = moveInApplication.getViewingDateTime().plusDays(1);
        var rescheduleRequest = new RescheduleRequest(updatedViewingDateTime);
        var moveInApplicationId = moveInApplication.getId();

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        // when
        service.rescheduleViewing(moveInApplicationId, rescheduleRequest);

        // then
        assertEquals(updatedViewingDateTime, moveInApplication.getViewingDateTime());
        verify(repository).save(moveInApplication);
    }

    @Test
    void setDecision_ThrowsWhenMoveInApplicationDoesNotExist() {
        // given
        var moveInApplicationId = 1L;
        var decisionRequest = new DecisionRequest(MoveInApplicationStatus.APPROVED, "");
        Jwt jwt = mock(Jwt.class);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.empty());

        // when & then
        var ex =  assertThrows(MoveInApplicationNotFoundException.class, () -> service.setDecision(moveInApplicationId, decisionRequest, jwt));
        assertEquals(String.format("Move in application with id %d not found", moveInApplicationId), ex.getMessage());
    }

    @Test
    void setDecision_Landlord_SetsFieldsAndSaves() {
        // given
        long moveInApplicationId = 1L;
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();

        moveInApplication.setRentierDecidedAt(null);
        moveInApplication.setRentierDecisionReason(null);

        var landlordDecision = MoveInApplicationStatus.APPROVED;
        var request = new DecisionRequest(landlordDecision, "OK by landlord");
        Jwt jwt = mock(Jwt.class);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        try (MockedStatic<JwtUtils> roles = Mockito.mockStatic(JwtUtils.class)) {
            roles.when(() -> JwtUtils.getRoles(jwt)).thenReturn(new HashSet<>(Set.of("LANDLORD")));

            // when
            service.setDecision(moveInApplicationId, request, jwt);

            // then
            assertEquals(landlordDecision, moveInApplication.getLandlordStatus());
            assertEquals("OK by landlord", moveInApplication.getLandlordDecisionReason());
            assertNotNull(moveInApplication.getLandlordDecidedAt(), "landlordDecidedAt should be set");
            assertEquals(MoveInApplicationStatus.PENDING, moveInApplication.getRentierStatus());
            assertNull(moveInApplication.getRentierDecidedAt());
            assertNull(moveInApplication.getRentierDecisionReason());

            verify(repository).save(moveInApplication);
        }
    }

    @Test
    void setDecision_Rentier_ThrowsIfNotCancelledAndLandlordNotApproved() {
        // given
        long moveInApplicationId = 1L;
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();

        var rentierDecision = MoveInApplicationStatus.APPROVED;
        var request = new DecisionRequest(rentierDecision, "OK by rentier");
        Jwt jwt = mock(Jwt.class);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        try (MockedStatic<JwtUtils> roles = Mockito.mockStatic(JwtUtils.class)) {
            roles.when(() -> JwtUtils.getRoles(jwt)).thenReturn(new HashSet<>(Set.of("RENTIER")));

            // when & then
            var ex =  assertThrows(LandlordDecisionRequiredException.class, () -> service.setDecision(moveInApplicationId, request, jwt));
            assertEquals("Landlord must approve the application before this action can be performed", ex.getMessage());

        }
    }

    @Test
    void setDecision_Rentier_SetsFieldsAndSaves() {
        // given
        long moveInApplicationId = 1L;
        var moveInApplication = MoveInApplicationFixtures.pendingMoveInApplication();

        moveInApplication.setLandlordStatus(MoveInApplicationStatus.APPROVED);

        var rentierDecision = MoveInApplicationStatus.APPROVED;
        var request = new DecisionRequest(rentierDecision, "OK by rentier");
        Jwt jwt = mock(Jwt.class);

        when(repository.findById(moveInApplicationId)).thenReturn(Optional.of(moveInApplication));

        try (MockedStatic<JwtUtils> roles = Mockito.mockStatic(JwtUtils.class)) {
            roles.when(() -> JwtUtils.getRoles(jwt)).thenReturn(new HashSet<>(Set.of("RENTIER")));

            // when
            service.setDecision(moveInApplicationId, request, jwt);

            // then
            assertEquals(rentierDecision, moveInApplication.getRentierStatus());
            assertEquals("OK by rentier", moveInApplication.getRentierDecisionReason());
            assertNotNull(moveInApplication.getRentierDecidedAt(), "rentierDecidedAt should be set");
            assertEquals(MoveInApplicationStatus.APPROVED, moveInApplication.getLandlordStatus());

            verify(repository).save(moveInApplication);
        }
    }
}
