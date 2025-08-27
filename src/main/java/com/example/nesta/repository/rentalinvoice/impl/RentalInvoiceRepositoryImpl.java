package com.example.nesta.repository.rentalinvoice.impl;

import com.example.nesta.dto.rentalinvoice.RentalInvoiceListItemDto;
import com.example.nesta.dto.rentalinvoice.RentalInvoiceQuery;
import com.example.nesta.repository.rentalinvoice.RentalInvoiceRepositoryQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static com.example.nesta.model.QRentalInvoice.rentalInvoice;

@Repository
@RequiredArgsConstructor
public class RentalInvoiceRepositoryImpl implements RentalInvoiceRepositoryQuery {

    private final JPAQueryFactory qf;

    @Override
    public Page<RentalInvoiceListItemDto> searchMine(RentalInvoiceQuery q, String userId) {
        int page = q.page() != null ? q.page() : 0;
        int size = q.size() != null ? q.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var where = new BooleanBuilder()
                .and(rentalInvoice.issuerId.eq(userId).or(rentalInvoice.receiverId.eq(userId)));

        if (q.number() != null && !q.number().isBlank()) {
            where.and(rentalInvoice.number.containsIgnoreCase(q.number()));
        }
        if (q.paid() != null) {
            where.and(rentalInvoice.paid.eq(q.paid()));
        }
        if (q.from() != null) {
            where.and(rentalInvoice.createdAt.goe(q.from()));
        }
        if (q.to() != null) {
            where.and(rentalInvoice.createdAt.loe(q.to()));
        }
        if (q.offerId() != null) {
            where.and(rentalInvoice.rentalOffer.id.eq(q.offerId()));
        }

        var sort = pageable.getSort().stream().findFirst().orElse(Sort.Order.desc("createdAt"));
        var direction = sort.isAscending() ? com.querydsl.core.types.Order.ASC
                : com.querydsl.core.types.Order.DESC;
        var orderBy = switch (sort.getProperty()) {
            case "createdAt"    -> new OrderSpecifier<>(direction, rentalInvoice.createdAt);
            case "amountCents" -> new OrderSpecifier<>(direction, rentalInvoice.amountCents);
            default          -> new OrderSpecifier<>(direction, rentalInvoice.paid);
        };

        var baseQuery = qf.select(Projections.constructor(
                        RentalInvoiceListItemDto.class,
                        rentalInvoice.id,
                        rentalInvoice.number,
                        rentalInvoice.amountCents,
                        rentalInvoice.currency,
                        rentalInvoice.paid,
                        rentalInvoice.createdAt,
                        rentalInvoice.paidAt,
                        rentalInvoice.rentalOffer.id
                ))
                .from(rentalInvoice)
                .where(where);

        var content = baseQuery
                .orderBy(orderBy)
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = qf.select(rentalInvoice.count())
                .from(rentalInvoice)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, Objects.requireNonNullElse(total, 0L));
    }
}