package com.dbproject.backend.web;

import com.dbproject.backend.dto.AddAddressRequest;
import com.dbproject.backend.entity.Address;
import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.service.AddressService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService=addressService;
    }

    @PostMapping
    public ResponseEntity<Void> addAddress(@RequestBody AddAddressRequest request) {
        addressService.addAddress(request);
        return ResponseEntity.status(201).build();
    }
}
