package com.mini_project.miniproject.orders.service;

import com.mini_project.miniproject.events.dto.PaginatedEventResponseDto;
import com.mini_project.miniproject.orders.dto.*;
import org.springframework.security.core.Authentication;

public interface OrderService {
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication);
    void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication);
    OrderDetailsDTO getOrderDetails(Long orderId, Authentication authentication);
    PaginatedOrderDetailsDTO getPaginatedOrderDetails(Authentication authentication, int page, int size);
}
