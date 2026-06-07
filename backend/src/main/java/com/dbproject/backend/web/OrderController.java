package com.dbproject.backend.web;

import com.dbproject.backend.dto.CreateOrderRequest;
import com.dbproject.backend.dto.OrderDto;
import com.dbproject.backend.dto.PayOrderRequest;
import com.dbproject.backend.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest, HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        orderService.createOrder(customerId, createOrderRequest);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> findByCustomerId(HttpSession session){
        Integer customerId = SessionUtils.requireCustomerId(session);
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> payOrder(
            @PathVariable Integer orderId,
            @RequestBody PayOrderRequest request,
            HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        orderService.payOrder(customerId, orderId, request.getPaymentMethodId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer orderId, HttpSession session) {
        Integer customerId = SessionUtils.requireCustomerId(session);
        orderService.cancelOrder(customerId, orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<Void> orderDelivered(@PathVariable Integer orderId) {
        orderService.orderDelivered(orderId);
        return ResponseEntity.ok().build();
    }
}
