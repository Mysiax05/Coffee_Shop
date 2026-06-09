package com.dbproject.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Integer customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
