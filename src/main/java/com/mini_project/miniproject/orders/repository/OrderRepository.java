package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.dto.OrderDetailsForOrganizerDTO;
import com.mini_project.miniproject.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndStatus(Long orderId, boolean status);
    Page<Orders> findByCustomerIdAndStatus(Long userId, boolean status, Pageable pageable);
//    Page<Orders> findByOrganizerId(Long organizerId, Pageable pageable);

//    @Query("SELECT o FROM Orders o JOIN Event e ON o.eventId = e.id WHERE e.organizerId = :organizerId")
//    Page<Orders> findByEventOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

//    @Query("SELECT o FROM Orders o JOIN Events e ON o.eventId = e.id WHERE e.organizer.id = :organizerId")
//    Page<Orders> findOrdersByEventOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

    @Query("SELECT o FROM Orders o JOIN Events e ON o.eventId = e.id WHERE e.organizer.id = :organizerId AND o.status = true")
    Page<Orders> findPaidOrdersByEventOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

}
