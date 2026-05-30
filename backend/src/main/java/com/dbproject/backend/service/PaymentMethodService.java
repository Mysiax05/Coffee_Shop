package com.dbproject.backend.service;

import com.dbproject.backend.entity.PaymentMethod;
import com.dbproject.backend.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository){
        this.paymentMethodRepository=paymentMethodRepository;
    }

    public List<PaymentMethod> getAll(){
        return paymentMethodRepository.findAll();
    }
}
