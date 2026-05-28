package com.dbproject.backend.service;

import com.dbproject.backend.dto.AddAddressRequest;
import com.dbproject.backend.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void addAddress(AddAddressRequest request) {
        addressRepository.addAddress(
                request.getCustomerId(),
                request.getLabel(),
                request.getStreet(),
                request.getCity(),
                request.getPostalCode(),
                request.getCountry()
        );
    }
}