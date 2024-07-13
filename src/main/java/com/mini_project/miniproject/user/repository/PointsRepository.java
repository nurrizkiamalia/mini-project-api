package com.mini_project.miniproject.user.repository;

import com.mini_project.miniproject.user.entity.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
//    List<Points> findAllByUserIdAndExpiryDateAfter(Long userId, LocalDate now);
    List<Points> findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(Long userId, LocalDate now);


}
