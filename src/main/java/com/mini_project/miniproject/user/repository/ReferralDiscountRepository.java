package com.mini_project.miniproject.user.repository;

import com.mini_project.miniproject.user.entity.ReferralDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReferralDiscountRepository extends JpaRepository<ReferralDiscount, Long> {
    ReferralDiscount findByUserIdAndExpiryDateAfter(Long userId, LocalDate date);
    void deleteByUserId(Long userId);

//    ReferralDiscount findByUserId(Long userId);
}
