package com.dbproject.backend.web;

import com.dbproject.backend.dto.AddAddressRequest;
import com.dbproject.backend.dto.AddressDto;
import com.dbproject.backend.service.AddressService;

import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<Void> addAddress(@RequestBody AddAddressRequest request, HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        addressService.addAddress(customerId, request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> findActiveByCustomerId(HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        return ResponseEntity.ok(addressService.findActiveByCustomerId(customerId));
    }

    @PatchMapping("/{addressId}/deactivate")
    public ResponseEntity<Void> deactivateAddress(@PathVariable Integer addressId, HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        addressService.deactivateAddress(addressId, customerId);
        return ResponseEntity.status(200).build();
    }
}
