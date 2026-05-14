package com.dbproject.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid")
    private Integer customerId;


    @NotBlank
    @Size(max = 100)
    @Column(name = "firstname", nullable = false, length = 100)
    private String firstName;


    @NotBlank
    @Size(max = 100)
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastName;

    @NotBlank
    @Size(max = 200)
    @Email
    @Column(name = "email", unique = true, length = 200)
    private String email;


    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    //security will be done later, for now just string
    @Size(max = 200)
    @Column(name = "passwordhash", length = 200)
    private String passwordHash;

    @Generated(event = EventType.INSERT)
    @Column(name = "createdat", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
