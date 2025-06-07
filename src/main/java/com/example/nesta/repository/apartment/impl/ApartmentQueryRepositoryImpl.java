package com.example.nesta.repository.apartment.impl;

import com.example.nesta.dto.ApartmentFilter;
import com.example.nesta.model.Apartment;
import com.example.nesta.model.QApartment;
import com.example.nesta.query.ApartmentPredicateBuilder;
import com.example.nesta.repository.apartment.ApartmentQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ApartmentQueryRepositoryImpl implements ApartmentQueryRepository {
    private final JPAQueryFactory queryFactory;

    public ApartmentQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Apartment> searchApartments(ApartmentFilter filter) {
        QApartment apartment = QApartment.apartment;

        var apartmentPredicates = ApartmentPredicateBuilder.build(filter, apartment);

        if (apartmentPredicates.length == 0) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(apartment)
                .where(apartmentPredicates)
                .fetch();
    }
}
