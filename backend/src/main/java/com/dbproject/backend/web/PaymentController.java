package com.dbproject.backend.web;

import com.dbproject.backend.dto.PaymentDto;
import com.dbproject.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> findAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }
}
