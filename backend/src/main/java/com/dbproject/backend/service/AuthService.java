package com.dbproject.backend.service;

import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.exception.UnauthorizedException;
import com.dbproject.backend.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer authenticate(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Passwordless account (e.g. guests / seeded data) -> email alone is enough.
        if (customer.getPasswordHash() == null) {
            return customer;
        }

        // Account with a password -> the password must match.
        if (password == null || !passwordEncoder.matches(password, customer.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return customer;
    }

    public Customer getById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new UnauthorizedException("Session is no longer valid"));
    }
}
