package com.mini_project.miniproject.orders.repository;

<<<<<<< HEAD
import com.mini_project.miniproject.orders.dto.OrderDetailsForOrganizerDTO;
=======
>>>>>>> feature-dashboard
import com.mini_project.miniproject.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> feature-dashboard
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndStatus(Long orderId, boolean status);
    Page<Orders> findByCustomerIdAndStatus(Long userId, boolean status, Pageable pageable);

    @Query("SELECT o FROM Orders o JOIN Events e ON o.eventId = e.id WHERE e.organizer.id = :organizerId AND o.status = true")
    Page<Orders> findPaidOrdersByEventOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

    @Query("SELECT e.category, SUM(oi.quantity), SUM(o.totalPrice) " +
            "FROM Orders o " +
            "JOIN Events e ON o.eventId = e.id " +
            "JOIN OrderItems oi ON oi.order.id = o.id " +
            "WHERE e.organizer.id = :organizerId AND o.status = true " +
            "GROUP BY e.category")
    List<Object[]> getSalesDataByOrganizerId(@Param("organizerId") Long organizerId);

    @Query("SELECT e.name, SUM(o.totalPrice) " +
            "FROM Orders o " +
            "JOIN Events e ON o.eventId = e.id " +
            "WHERE e.organizer.id = :organizerId AND o.status = true " +
            "GROUP BY e.id, e.name " +
            "ORDER BY SUM(o.totalPrice) DESC")
    List<Object[]> getRevenueByEventForOrganizer(@Param("organizerId") Long organizerId);
}
