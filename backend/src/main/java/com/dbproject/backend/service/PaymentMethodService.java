package com.dbproject.backend.service;

import com.dbproject.backend.entity.PaymentMethod;
import com.dbproject.backend.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository){
        this.paymentMethodRepository=paymentMethodRepository;
    }

    public List<PaymentMethod> getAllActive(){
        return paymentMethodRepository.findAll()
                .stream()
                .filter(PaymentMethod::getIsActive)
                .collect(Collectors.toList());
    }

    public void deactivatePaymentMethod(Integer paymentMethodId){
        paymentMethodRepository.deactivatePaymentMethod(paymentMethodId);
    }
}
