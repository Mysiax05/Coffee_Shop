package com.dbproject.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Integer addressId;
    private List<OrderItemSmaller> items;

    @Data
    public static class OrderItemSmaller {
        private Integer productId;
        private Integer quantity;
    }

}