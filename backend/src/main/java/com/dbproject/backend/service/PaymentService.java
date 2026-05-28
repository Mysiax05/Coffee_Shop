package com.dbproject.backend.service;

import com.dbproject.backend.dto.PaymentDto;
import com.dbproject.backend.entity.Payment;
import com.dbproject.backend.exception.ResourceNotFoundException;
import com.dbproject.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public PaymentDto findById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Payment with ID %d was not found", paymentId)));
        return toDto(payment);
    }

    public PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrder().getOrderId());
        dto.setPaymentMethodId(payment.getPaymentMethod().getPaymentMethodId());
        dto.setPaymentMethodType(payment.getPaymentMethod().getType());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}