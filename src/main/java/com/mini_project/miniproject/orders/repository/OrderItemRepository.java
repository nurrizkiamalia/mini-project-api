package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findByOrderId(Long orderId);
}
