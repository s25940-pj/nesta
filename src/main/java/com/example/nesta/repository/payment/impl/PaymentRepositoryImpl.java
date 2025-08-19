package com.example.nesta.repository.payment.impl;

import com.example.nesta.repository.payment.PaymentRepositoryQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.nesta.payment.api.PaymentListItemDto;
import com.example.nesta.payment.api.PaymentQuery;
import java.util.Objects;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryQuery {

    private final JPAQueryFactory qf;

    @Override
    public Page<PaymentListItemDto> searchMine(PaymentQuery q, String userId) {
        int page = q.page() != null ? q.page() : 0;
        int size = q.size() != null ? q.size() : 20;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var where = new BooleanBuilder()
                .and(payment.userId.eq(userId));

        if (q.status() != null && !q.status().isBlank()) {
            where.and(payment.status.stringValue().equalsIgnoreCase(q.status()));
        }
        if (q.from() != null) {
            where.and(payment.createdAt.goe(q.from()));
        }
        if (q.to() != null) {
            where.and(payment.createdAt.loe(q.to()));
        }
        if (q.invoiceId() != null) {
            where.and(payment.invoiceId.eq(q.invoiceId()));
        }

        var baseQuery = qf.select(Projections.constructor(
                        PaymentListItemDto.class,
                        payment.id,
                        payment.sessionId,
                        payment.status.stringValue(),
                        payment.amountCents,
                        payment.currency,
                        payment.method,
                        payment.invoiceId,
                        payment.createdAt,
                        payment.paidAt
                ))
                .from(payment)
                .where(where);

        // default sort by createdAt
        var sort = pageable.getSort().stream().findFirst().orElse(Sort.Order.desc("createdAt"));
        var direction = sort.isAscending() ? com.querydsl.core.types.Order.ASC
                : com.querydsl.core.types.Order.DESC;
        var orderBy = switch (sort.getProperty()) {
            case "paidAt"    -> new com.querydsl.core.types.OrderSpecifier<>(direction, payment.paidAt);
            case "amountCents" -> new com.querydsl.core.types.OrderSpecifier<>(direction, payment.amountCents);
            default          -> new com.querydsl.core.types.OrderSpecifier<>(direction, payment.createdAt);
        };

        var content = baseQuery
                .orderBy(orderBy)
                .offset((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = qf.select(payment.count())
                .from(payment)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, Objects.requireNonNullElse(total, 0L));
    }
}