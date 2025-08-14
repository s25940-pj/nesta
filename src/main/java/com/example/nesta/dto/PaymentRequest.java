package com.example.nesta.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long invoiceId;
    private BigDecimal amount;
    private String method;
}