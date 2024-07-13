package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.entity.OrderDiscounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDiscountRepository extends JpaRepository<OrderDiscounts, Long> {
}
