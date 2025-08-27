package com.example.nesta.repository.payment;

import com.example.nesta.payment.api.PaymentListItemDto;
import com.example.nesta.payment.api.PaymentQuery;
import org.springframework.data.domain.Page;

public interface PaymentRepositoryQuery {
    Page<PaymentListItemDto> searchMine(PaymentQuery q, String userId);
}
