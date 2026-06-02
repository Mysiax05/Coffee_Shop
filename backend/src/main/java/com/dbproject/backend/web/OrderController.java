package com.dbproject.backend.web;

import com.dbproject.backend.dto.CreateOrderRequest;
import com.dbproject.backend.dto.OrderDto;
import com.dbproject.backend.dto.PayOrderRequest;
import com.dbproject.backend.entity.Customer;
import com.dbproject.backend.entity.Order;
import com.dbproject.backend.service.OrderService;
import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Or;
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
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        orderService.createOrder(createOrderRequest);
        return ResponseEntity.status(201).build();

    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> findByCustomerId(@PathVariable Integer customerId){
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> payOrder(
            @PathVariable Integer orderId,
            @RequestBody PayOrderRequest request) {
        orderService.payOrder(request.getCustomerId(), orderId, request.getPaymentMethodId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam Integer customerId) {
        orderService.cancelOrder(customerId, orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<Void> orderDelivered(
            @PathVariable Integer orderId) {
        orderService.orderDelivered(orderId);
        return ResponseEntity.ok().build();
    }
}
