package com.mini_project.miniproject.orders.controller;

import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.orders.dto.ConfirmPaymentRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderResponseDTO;
import com.mini_project.miniproject.orders.service.OrderService;
import com.mini_project.miniproject.responses.Response;
import org.hibernate.query.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<Response<Object>> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication){
        CreateOrderResponseDTO orderResponseDTO = orderService.createOrder(createOrderRequestDTO, authentication);
        return Response.success("Order created successfully", orderResponseDTO);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Response<String>> confirmPayment(@RequestBody ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication){
        orderService.confirmPayment(confirmPaymentRequestDTO, authentication);
        return Response.success("Order proceed successfully.");
    }
}
