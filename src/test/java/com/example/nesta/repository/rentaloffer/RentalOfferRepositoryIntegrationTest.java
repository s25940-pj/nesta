package com.example.nesta.repository.rentaloffer;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.fixtures.ApartmentFixtures;
import com.example.nesta.fixtures.RentalOfferFixtures;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.model.enums.EmploymentStatus;
import com.example.nesta.model.enums.FurnishingStatus;
import com.example.nesta.model.enums.PetPolicy;
import com.example.nesta.model.enums.SmokingPolicy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RentalOfferRepositoryIntegrationTest {
    @Autowired
    private RentalOfferRepository rentalOfferRepository;

    @Autowired
    private TestEntityManager entityManager;

    @TestConfiguration
    static class QueryDslTestConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Test
    public void shouldFilterBy_landlordId() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setLandlordId(String.valueOf(1));
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);

        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setLandlordId(String.valueOf(2));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setLandlordId(match.getLandlordId());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_apartment() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        apartmentForMatch.setLandlordId(String.valueOf(1));
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        apartmentForMatch.setLandlordId(String.valueOf(2));
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        entityManager.persist(other);

        var rentalOfferFilter = new RentalOfferFilter();
        var modelMapper = new ModelMapper();
        rentalOfferFilter.setApartment(modelMapper.map(apartmentForMatch, ApartmentFilter.class));

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(rentalOfferFilter);

        // then
        assertEquals(1, result.size());
    }


    @Test
    public void shouldFilterBy_monthlyRent() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setMonthlyRent(BigDecimal.valueOf(1));
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setMonthlyRent(BigDecimal.valueOf(2));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setMonthlyRent(match.getMonthlyRent());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_deposit() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setDeposit(BigDecimal.valueOf(1));
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setDeposit(BigDecimal.valueOf(2));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setDeposit(match.getDeposit());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_utilitiesCost() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setUtilitiesCost(BigDecimal.valueOf(1));
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setUtilitiesCost(BigDecimal.valueOf(2));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setUtilitiesCost(match.getUtilitiesCost());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_utilitiesIncluded() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setUtilitiesIncluded(true);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setUtilitiesIncluded(false);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setUtilitiesIncluded(match.isUtilitiesIncluded());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_availableFrom() {
        // given
        var now = LocalDate.now();

        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setAvailableFrom(now);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setAvailableFrom(now.minusDays(1));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setAvailableFrom(match.getAvailableFrom());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_availableUntil() {
        // given
        var now = LocalDate.now();

        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setAvailableUntil(now);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setAvailableUntil(now.minusDays(1));
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setAvailableUntil(match.getAvailableUntil());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_shortTermRental() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setShortTermRental(true);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setShortTermRental(false);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setShortTermRental(match.isShortTermRental());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_furnishingStatus() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setFurnishingStatus(FurnishingStatus.FURNISHED);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setFurnishingStatus(FurnishingStatus.PARTIALLY_FURNISHED);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setFurnishingStatus(match.getFurnishingStatus());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_preferredEmploymentStatus() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setPreferredEmploymentStatus(EmploymentStatus.EMPLOYED);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setPreferredEmploymentStatus(EmploymentStatus.STUDENT);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setPreferredEmploymentStatus(match.getPreferredEmploymentStatus());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_smokingPolicy() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setSmokingPolicy(SmokingPolicy.YES);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setSmokingPolicy(SmokingPolicy.NO);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setSmokingPolicy(match.getSmokingPolicy());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_petPolicy() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setPetPolicy(PetPolicy.YES);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setPetPolicy(PetPolicy.NO);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setPetPolicy(match.getPetPolicy());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_accessibleForDisabled() {
        // given
        var apartmentForMatch = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForMatch);
        var match = RentalOfferFixtures.rentalOffer(apartmentForMatch);
        match.setAccessibleForDisabled(true);
        entityManager.persist(match);

        var apartmentForOther = ApartmentFixtures.apartment();
        entityManager.persist(apartmentForOther);
        var other = RentalOfferFixtures.rentalOffer(apartmentForOther);
        other.setAccessibleForDisabled(false);
        entityManager.persist(other);

        var filter = new RentalOfferFilter();
        filter.setAccessibleForDisabled(match.isAccessibleForDisabled());

        // when
        List<RentalOffer> result = rentalOfferRepository.searchRentalOffers(filter);

        // then
        assertEquals(1, result.size());
    }
}
