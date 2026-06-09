package com.dbproject.backend.web;

import com.dbproject.backend.entity.PaymentMethod;
import com.dbproject.backend.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentmethods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService){
        this.paymentMethodService=paymentMethodService;
    }
    @GetMapping()
    public ResponseEntity<List<PaymentMethod>> getAllActive() {
        return ResponseEntity.ok(paymentMethodService.getAllActive());
    }

    @PatchMapping("/{paymentMethodId}/deactivate")
    public ResponseEntity<Void> deactivatePaymentMethod(@PathVariable Integer paymentMethodId){
        paymentMethodService.deactivatePaymentMethod(paymentMethodId);
        return ResponseEntity.status(200).build();
    }
}
