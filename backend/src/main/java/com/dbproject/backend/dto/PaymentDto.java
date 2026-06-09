package com.dbproject.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Integer paymentId;
    private Integer orderId;
    private String orderStatus;
    private PaymentMethodDto paymentMethod;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodDto {
        private Integer paymentMethodId;
        private String provider;
        private String type;
    }
}