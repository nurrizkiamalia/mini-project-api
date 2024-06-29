package com.mini_project.miniproject.user.repository;

import com.mini_project.miniproject.user.entity.ReferralDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralDiscountRepository extends JpaRepository<ReferralDiscount, Long> {
}
