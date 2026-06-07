package com.dbproject.backend.web;

import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService=customerService;
    }


    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid Customer customer) {
        customerService.register(customer);
        return ResponseEntity.ok().build();
    }
}
