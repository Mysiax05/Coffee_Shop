package com.dbproject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "paymentmethods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentmethodid")
    private Integer paymentMethodId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @NotBlank
    @Size(max = 50)
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @NotNull
    @Column(name = "isactive", nullable = false)
    private Boolean isActive = true;
}