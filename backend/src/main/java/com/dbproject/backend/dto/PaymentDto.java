package com.dbproject.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Integer paymentId;
    private Integer orderId;
    private Integer paymentMethodId;
    private String paymentMethodType;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
