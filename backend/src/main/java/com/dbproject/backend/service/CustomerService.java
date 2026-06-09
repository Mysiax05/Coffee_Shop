package com.dbproject.backend.service;

import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.exception.EmailAlreadyExistsException;
import com.dbproject.backend.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager=entityManager;
    }

    @Transactional
    public void register(Customer customer){
        customerRepository.findByEmail(customer.getEmail())
                .filter(existing -> existing.getPasswordHash() != null)
                .ifPresent(existing -> {
                    throw new EmailAlreadyExistsException("Email already exists");
                });

        String hashedPassword =
                passwordEncoder.encode(customer.getPasswordHash());

        entityManager.createNativeQuery("""
            CALL p_register_customer(
                :firstname,
                :lastname,
                :email,
                :phone,
                :passwordhash
            )
        """)
                .setParameter("firstname", customer.getFirstName())
                .setParameter("lastname", customer.getLastName())
                .setParameter("email", customer.getEmail())
                .setParameter("phone", customer.getPhone())
                .setParameter("passwordhash", hashedPassword)
                .executeUpdate();
    }

    public void changeEmail(Integer customerId, String newEmail) {
        customerRepository.changeEmail(customerId, newEmail);
    }
}
