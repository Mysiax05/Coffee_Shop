package com.dbproject.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BestSellerDto {
    private Integer productId;
    private String name;
    private Integer totalSold;
    private BigDecimal revenue;
}