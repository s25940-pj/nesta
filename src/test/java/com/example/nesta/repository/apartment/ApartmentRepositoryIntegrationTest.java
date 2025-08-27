package com.example.nesta.repository.apartment;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.fixtures.ApartmentFixtures;
import com.example.nesta.model.enums.ParkingType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class ApartmentRepositoryIntegrationTest {
    @Autowired
    private ApartmentRepository apartmentRepository;

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
        var match = ApartmentFixtures.apartment();
        match.setLandlordId(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setLandlordId(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setLandlordId(match.getLandlordId());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_area() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setArea(1);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setArea(2);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setArea(match.getArea());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_numberOfRooms() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setNumberOfRooms(1);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setNumberOfRooms(2);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setNumberOfRooms(match.getNumberOfRooms());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_numberOfBathrooms() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setNumberOfBathrooms(1);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setNumberOfBathrooms(2);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setNumberOfBathrooms(match.getNumberOfBathrooms());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_floor() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setFloor(1);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setFloor(2);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setFloor(match.getFloor());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_furnished() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setFurnished(true);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setFurnished(false);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setFurnished(match.isFurnished());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_hasBalcony() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setHasBalcony(true);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setHasBalcony(false);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setHasBalcony(match.isHasBalcony());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_parkingType() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setParkingType(ParkingType.NONE);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setParkingType(ParkingType.UNDERGROUND);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setParkingType(match.getParkingType());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_hasElevator() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setHasElevator(true);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setHasElevator(false);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setHasElevator(match.isHasElevator());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_isDisabledAccessible() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setDisabledAccessible(true);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setDisabledAccessible(false);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setIsDisabledAccessible(match.isDisabledAccessible());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_hasStorageRoomInBasement() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setHasStorageRoomInBasement(true);
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setHasStorageRoomInBasement(false);
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setHasStorageRoomInBasement(match.isHasStorageRoomInBasement());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_streetName() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setStreetName(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setStreetName(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setStreetName(match.getStreetName());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_buildingNumber() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setBuildingNumber(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setBuildingNumber(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setBuildingNumber(match.getBuildingNumber());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_apartmentNumber() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setApartmentNumber(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setApartmentNumber(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setApartmentNumber(match.getApartmentNumber());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_city() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setCity(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setCity(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setCity(match.getCity());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_postalCode() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setPostalCode(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setPostalCode(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setPostalCode(match.getPostalCode());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFilterBy_country() {
        // given
        var match = ApartmentFixtures.apartment();
        match.setCountry(String.valueOf(1));
        entityManager.persist(match);

        var other = ApartmentFixtures.apartment();
        other.setCountry(String.valueOf(2));
        entityManager.persist(other);

        var filter = new ApartmentFilter();
        filter.setCountry(match.getCountry());

        // when
        var result = apartmentRepository.searchApartments(filter);

        // then
        assertEquals(1, result.size());
    }
}
