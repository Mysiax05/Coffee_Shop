package com.dbproject.backend.service;

import com.dbproject.backend.entity.PaymentMethod;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> getAll() {
        return paymentMethodRepository
                .findAll();
    }

    public PaymentMethod findById(Integer paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Payment method with ID %d was not found", paymentMethodId)
                ));
    }
}
