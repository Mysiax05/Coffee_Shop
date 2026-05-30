package com.dbproject.backend.service;

import com.dbproject.backend.dto.OrderDto;
import com.dbproject.backend.dto.PaymentDto;
import com.dbproject.backend.entity.Payment;
import com.dbproject.backend.repository.PaymentRepository;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository=paymentRepository;
    }

    public List<PaymentDto> findByCustomerId(Integer customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    private PaymentDto toDto(Payment payment){
        PaymentDto dto = new PaymentDto();

        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrder().getOrderId());
        dto.setOrderStatus(payment.getOrder().getStatus());
        dto.setAmount(payment.getAmount());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setPaidAt(payment.getPaidAt());

        dto.setPaymentMethod(new PaymentDto.PaymentMethodDto());
        dto.getPaymentMethod().setPaymentMethodId(payment.getPaymentMethod().getPaymentMethodId());
        dto.getPaymentMethod().setProvider(payment.getPaymentMethod().getProvider());
        dto.getPaymentMethod().setType(payment.getPaymentMethod().getType());

        return dto;
    }

    public BigDecimal findCustomerExpenses(Integer customerId) {
        return paymentRepository.findCustomerExpenses(customerId);
    }
}
