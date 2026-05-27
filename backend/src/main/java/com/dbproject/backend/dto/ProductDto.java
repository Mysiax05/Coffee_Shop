package com.dbproject.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Integer productId;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer stock;
    private boolean isActive;
    private String attributes;
}
