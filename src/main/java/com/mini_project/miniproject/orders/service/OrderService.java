package com.mini_project.miniproject.orders.service;

import com.mini_project.miniproject.orders.dto.ConfirmPaymentRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderResponseDTO;
import com.mini_project.miniproject.orders.dto.OrderDetailsDTO;
import org.springframework.security.core.Authentication;

public interface OrderService {
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication);
    void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication);

    OrderDetailsDTO getOrderDetails(Long orderId, Authentication authentication);
}
