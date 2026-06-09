package com.dbproject.backend.web;

import com.dbproject.backend.dto.ChangeEmailRequest;
import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.service.CustomerService;
import jakarta.servlet.http.HttpSession;
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

    @PatchMapping("/changemail")
    public ResponseEntity<Void> changeMail(@RequestBody @Valid ChangeEmailRequest request, HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        customerService.changeEmail(customerId, request.getNewEmail());
        return ResponseEntity.ok().build();
    }
}
