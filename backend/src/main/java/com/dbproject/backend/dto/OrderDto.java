package com.dbproject.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({"orderId", "customerId", "status", "orderDate", "shipDate", "address", "items", "totalOrderCost"})
public class OrderDto {

    private Integer orderId;
    private Integer customerId;
    private AddressSummaryDto address;
    private String status;
    private LocalDateTime orderDate;
    private LocalDateTime shipDate;
    private List<OrderItem> items;

    public BigDecimal getTotalOrderCost() {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;
        return items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressSummaryDto {
        private Integer addressId;
        private String street;
        private String city;
        private String postalCode;
        private String country;
    }



}
