package com.dbproject.backend.service;

import com.dbproject.backend.dto.PaymentMethodDto;
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

    public List<PaymentMethodDto> getAll() {
        return paymentMethodRepository
                .findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PaymentMethodDto findById(Integer paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Payment method with ID %d was not found", paymentMethodId)
                ));
        return toDto(paymentMethod);
    }

    public PaymentMethodDto toDto(PaymentMethod paymentMethod) {
        PaymentMethodDto paymentMethodDto = new PaymentMethodDto();
        paymentMethodDto.setPaymentMethodId(paymentMethod.getPaymentMethodId());
        paymentMethodDto.setProvider(paymentMethod.getProvider());
        paymentMethodDto.setPaymentMethodType(paymentMethod.getType());
        paymentMethodDto.setActive(paymentMethod.getIsActive());
        return paymentMethodDto;
    }
}
