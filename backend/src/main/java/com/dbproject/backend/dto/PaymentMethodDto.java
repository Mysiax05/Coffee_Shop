package com.dbproject.backend.dto;

import lombok.Data;

@Data
public class PaymentMethodDto {
    private Integer paymentMethodId;
    private String provider;
    private String paymentMethodType;
    private boolean isActive;
}
