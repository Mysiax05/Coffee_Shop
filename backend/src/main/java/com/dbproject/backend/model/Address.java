package com.dbproject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressid")
    private Integer addressId;

    @ManyToOne
    @JoinColumn(name = "customerid", nullable = false)
    private Customer customer;

    @Size(max = 100)
    @Column(name = "label", length = 100)
    private String label;

    @NotBlank
    @Size(max = 100)
    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @NotBlank
    @Size(max = 100)
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Size(max = 20)
    @Column(name = "postalcode", nullable = false, length = 20)
    private String postalCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "country", nullable = false, length = 100)
    private String country = "Poland";

    @NotNull
    @Column(name = "isactive", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(name = "isdefault", nullable = false)
    private Boolean isDefault = false;
}
