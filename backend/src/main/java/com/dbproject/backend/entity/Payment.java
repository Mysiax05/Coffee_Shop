package com.dbproject.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import java.time.LocalDateTime;
import java.math.BigDecimal;


@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentid")
    private Integer paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderid", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentmethodid", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull
    @Positive
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 50)
    @Column(name = "status", nullable = false, length = 50)
    private String status = "pending";

    @Generated(event = EventType.INSERT)
    @Column(name = "createdat", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paidat")
    private LocalDateTime paidAt;
}
