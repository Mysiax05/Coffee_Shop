package com.dbproject.backend.web;

import com.dbproject.backend.dto.PaymentDto;
import com.dbproject.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentDto>> findByCustomerId(@PathVariable Integer customerId){
        return ResponseEntity.ok(paymentService.findByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/total")
    public ResponseEntity<BigDecimal> findCustomerExpense(@PathVariable Integer customerId){
        return ResponseEntity.ok(paymentService.findCustomerExpenses(customerId));
    }
}
