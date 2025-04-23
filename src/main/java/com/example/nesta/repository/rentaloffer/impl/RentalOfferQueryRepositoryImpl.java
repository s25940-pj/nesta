package com.example.nesta.repository.rentaloffer.impl;

import com.example.nesta.dto.RentalOfferFilter;
import com.example.nesta.model.QRentalOffer;
import com.example.nesta.model.RentalOffer;
import com.example.nesta.query.ApartmentPredicateBuilder;
import com.example.nesta.query.RentalOfferPredicateBuilder;
import com.example.nesta.repository.rentaloffer.RentalOfferQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
public class RentalOfferQueryRepositoryImpl implements RentalOfferQueryRepository {
    private final JPAQueryFactory queryFactory;

    public RentalOfferQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<RentalOffer> searchRentalOffers(RentalOfferFilter filter) {
        QRentalOffer rentalOffer = QRentalOffer.rentalOffer;

        var rentalOfferPredicates = RentalOfferPredicateBuilder.build(filter, rentalOffer);
        var apartmentPredicates = ApartmentPredicateBuilder.build(filter.getApartment(), rentalOffer.apartment);
        var allPredicates = Stream.concat(
                Arrays.stream(rentalOfferPredicates),
                Arrays.stream(apartmentPredicates)
        ).filter(Objects::nonNull).toArray(BooleanExpression[]::new);

        if (allPredicates.length == 0) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(rentalOffer)
                .where(allPredicates)
                .fetch();
    }
}
