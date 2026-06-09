package com.dbproject.backend.service;

import com.dbproject.backend.dto.CreateOrderRequest;
import com.dbproject.backend.dto.OrderDto;
import com.dbproject.backend.dto.OrderItem;
import com.dbproject.backend.entity.Order;
import com.dbproject.backend.repository.OrderRepository;
import com.dbproject.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public void createOrder(Integer customerId, CreateOrderRequest request) {
        try {
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            orderRepository.createOrder(customerId, request.getAddressId(), itemsJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order items", e);
        }
    }

    public List<OrderDto> findByCustomerId(Integer customerId) {
        return orderRepository.findOrdersOfCustomer(customerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    public void payOrder(Integer customerId, Integer orderId, Integer paymentMethodId) {
        orderRepository.payOrder(customerId,orderId,paymentMethodId);
    }

    public void cancelOrder(Integer customerId, Integer orderId){
        orderRepository.cancelOrder(customerId,orderId);
    }


    private OrderDto toDTO(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomer().getCustomerId());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setShipDate(order.getShipDate());

        OrderDto.AddressSummaryDto addressDto = new OrderDto.AddressSummaryDto();
        addressDto.setAddressId(order.getAddress().getAddressId());
        addressDto.setStreet(order.getAddress().getStreet());
        addressDto.setCity(order.getAddress().getCity());
        addressDto.setPostalCode(order.getAddress().getPostalCode());
        addressDto.setCountry(order.getAddress().getCountry());
        dto.setAddress(addressDto);

        List<OrderItem> items = productRepository.findProductsOfOrder(order.getOrderId());
        dto.setItems(items);

        return dto;
    }

    public void orderDelivered(Integer orderId) {
        orderRepository.orderDelivered(orderId);
    }


//    public void orderDelivered(Integer orderId) {
//        orderRepository.payOrder(customerId,orderId,paymentMethodId);
//    }
}
