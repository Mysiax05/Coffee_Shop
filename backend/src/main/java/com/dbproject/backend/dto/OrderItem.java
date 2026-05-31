package com.dbproject.backend.dto;


import java.math.BigDecimal;


public interface OrderItem {
    Integer getProductId();
    String getName();
    Integer getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getTotal();
}
