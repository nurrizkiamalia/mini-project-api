package com.mini_project.miniproject.user.repository;

import com.mini_project.miniproject.user.entity.Points;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsRepository extends JpaRepository<Points, Long> {
}
