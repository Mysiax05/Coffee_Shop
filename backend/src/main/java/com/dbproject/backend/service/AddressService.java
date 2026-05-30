package com.dbproject.backend.service;

import com.dbproject.backend.dto.AddAddressRequest;
import com.dbproject.backend.dto.AddressDto;
import com.dbproject.backend.entity.Address;
import com.dbproject.backend.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<AddressDto> findByCustomerId(Integer customerId) {
        return addressRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AddressDto toDTO(Address address) {
        AddressDto dto = new AddressDto();
        dto.setAddressId(address.getAddressId());
        dto.setLabel(address.getLabel());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setIsDefault(address.getIsDefault());
        return dto;
    }
}