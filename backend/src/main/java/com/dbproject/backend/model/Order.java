package com.dbproject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressid", nullable = false)
    private Address address;

    @NotBlank
    @Size(max = 50)
    @Column(name = "status", nullable = false, length = 50)
    private String status = "pending";

    @Generated(event = EventType.INSERT)
    @Column(name = "orderdate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "shipdate")
    private LocalDateTime shipDate;
}
