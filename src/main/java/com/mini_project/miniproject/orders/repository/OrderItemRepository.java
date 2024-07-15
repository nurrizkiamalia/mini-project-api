package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findByOrderId(Long orderId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItems oi " +
            "JOIN Orders o ON oi.order.id = o.id " +
            "JOIN Events e ON o.eventId = e.id " +
            "WHERE e.organizer.id = :organizerId AND o.status = :status")
    Integer sumQuantityByOrganizerIdAndOrderStatus(@Param("organizerId") Long organizerId, @Param("status") boolean status);
}
