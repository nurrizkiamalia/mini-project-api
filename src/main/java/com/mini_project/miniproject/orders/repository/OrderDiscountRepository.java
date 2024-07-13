package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.dto.CreateOrderResponseDTO;
import com.mini_project.miniproject.orders.entity.OrderDiscounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDiscountRepository extends JpaRepository<OrderDiscounts, Long> {
    List<OrderDiscounts> findAllByOrderId(Long id);
}
