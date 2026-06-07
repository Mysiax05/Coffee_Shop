package com.dbproject.backend.web;

import com.dbproject.backend.dto.PaymentDto;
import com.dbproject.backend.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findByCustomerId(HttpSession session){
        Integer customerId = SessionUtils.requireCustomerId(session);
        return ResponseEntity.ok(paymentService.findByCustomerId(customerId));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> findCustomerExpense(HttpSession session){
        Integer customerId = SessionUtils.requireCustomerId(session);
        return ResponseEntity.ok(paymentService.findCustomerExpenses(customerId));
    }
}
