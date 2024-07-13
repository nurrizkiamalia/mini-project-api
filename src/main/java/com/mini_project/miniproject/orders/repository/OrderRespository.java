package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRespository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndStatus(Long orderId, boolean status);
}
