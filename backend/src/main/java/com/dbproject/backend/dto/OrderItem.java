package com.dbproject.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public interface OrderItem {
    Integer getProductId();
    String getName();
    Integer getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getTotal();
}
