package com.mini_project.miniproject.orders.repository;

import com.mini_project.miniproject.orders.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndStatus(Long orderId, boolean status);

//    Page<Orders> findByCustomerIdAndStatus(Long userId, boolean status, Pageable pageable);
    List<Orders> findByCustomerIdAndStatus(Long userId, boolean status);


//    @Query("SELECT o FROM Orders o JOIN Events e ON o.eventId = e.id WHERE e.organizer.id = :organizerId AND o.status = true")
//    Page<Orders> findPaidOrdersByEventOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);

    @Query("SELECT o FROM Orders o JOIN Events e ON o.eventId = e.id WHERE e.organizer.id = :organizerId AND o.status = true")
    List<Orders> findPaidOrdersByEventOrganizerId(@Param("organizerId") Long organizerId);


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

    @Query("SELECT YEAR(o.createdAt) as year, SUM(o.totalPrice) as revenue " +
            "FROM Orders o " +
            "JOIN Events e ON o.eventId = e.id " +
            "WHERE e.organizer.id = :organizerId AND o.status = true " +
            "GROUP BY YEAR(o.createdAt) " +
            "ORDER BY year DESC")
    List<Object[]> getYearlyRevenueForOrganizer(@Param("organizerId") Long organizerId);

    @Query("SELECT MONTH(o.createdAt) as month, SUM(o.totalPrice) as revenue " +
            "FROM Orders o " +
            "JOIN Events e ON o.eventId = e.id " +
            "WHERE e.organizer.id = :organizerId AND o.status = true AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) " +
            "ORDER BY month ASC")
    List<Object[]> getMonthlyRevenueForOrganizer(@Param("organizerId") Long organizerId, @Param("year") int year);

    @Query("SELECT DATE(o.createdAt) as date, SUM(o.totalPrice) as revenue " +
            "FROM Orders o " +
            "JOIN Events e ON o.eventId = e.id " +
            "WHERE e.organizer.id = :organizerId " +
            "AND o.status = true " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE) " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailyRevenueForCurrentMonth(@Param("organizerId") Long organizerId);
}

//    @Query("SELECT HOUR(o.createdAt) as hour, SUM(o.totalPrice) as revenue " +
//            "FROM Orders o " +
//            "JOIN Events e ON o.eventId = e.id " +
//            "WHERE e.organizer.id = :organizerId AND o.status = true AND DATE(o.createdAt) = CURRENT_DATE " +
//            "GROUP BY HOUR(o.createdAt) " +
//            "ORDER BY hour ASC")
//    List<Object[]> getTodayHourlyRevenueForOrganizer(@Param("organizerId") Long organizerId);
//}
