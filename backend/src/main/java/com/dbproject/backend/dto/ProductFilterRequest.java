package com.dbproject.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterRequest {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer categoryId;
    private String attributes;
}