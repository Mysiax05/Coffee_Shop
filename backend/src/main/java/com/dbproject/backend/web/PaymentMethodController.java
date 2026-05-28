package com.dbproject.backend.web;

import com.dbproject.backend.entity.PaymentMethod;
import com.dbproject.backend.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("api/paymentMethods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> findAll() {
        return ResponseEntity.ok(paymentMethodService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentMethodService.findById(id));
    }
}
