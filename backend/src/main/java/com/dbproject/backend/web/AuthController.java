package com.dbproject.backend.web;

import com.dbproject.backend.dto.CustomerDto;
import com.dbproject.backend.dto.LoginRequest;
import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerDto> login(@RequestBody @Valid LoginRequest request, HttpSession session) {
        Customer customer = authService.authenticate(request.getEmail(), request.getPassword());
        session.setAttribute(SessionUtils.CUSTOMER_ID, customer.getCustomerId());
        return ResponseEntity.ok(toDto(customer));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerDto> me(HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        return ResponseEntity.ok(toDto(authService.getById(customerId)));
    }

    private CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}
