package com.dbproject.backend.web;

import com.dbproject.backend.dto.AddAddressRequest;
import com.dbproject.backend.dto.AddressDto;
import com.dbproject.backend.dto.ProductDto;
import com.dbproject.backend.entity.Address;
import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.service.AddressService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AddressDto>> findByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.findByCustomerId(customerId));
    }
}
