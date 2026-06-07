package com.dbproject.backend.dto;

import lombok.Data;

@Data
public class AddAddressRequest {

    private String label;
    private String street;
    private String city;
    private String postalCode;
    private String country;

}
