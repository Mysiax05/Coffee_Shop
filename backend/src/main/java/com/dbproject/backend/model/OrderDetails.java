package com.dbproject.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "orderdetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetails {

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        @Column(name = "orderid")
        private Integer orderId;

        @Column(name = "productid")
        private Integer productId;
    }

    @EmbeddedId
    private Id id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "orderid", nullable = false)
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productid", nullable = false)
    private Product product;

    @NotNull
    @Positive
    @Column(name = "unitprice", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
