package com.mini_project.miniproject.orders.service;

//import com.mini_project.miniproject.events.dto.PaginatedEventResponseDto;
import com.mini_project.miniproject.orders.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {
    CreateOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication);
    void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication);
    OrderDetailsDTO getOrderDetails(Long orderId, Authentication authentication);
//    PaginatedOrderDetailsDTO getPaginatedOrderDetails(Authentication authentication, int page, int size);
//    PaginatedOrdersForOrganizerDTO getOrdersForOrganizer(Authentication authentication, int page, int size);
    OrderDetailsForOrganizerDTO getOrderDetailsForOrganizer(Long orderId, Authentication authentication);
    void cancelOrder(Long orderId, Authentication authentication);
    List<OrderDetailsDTO> getAllOrders(Authentication authentication);

    OrderListOrganizerDTO getAllOrdersForOrganizer(Authentication authentication);


}
