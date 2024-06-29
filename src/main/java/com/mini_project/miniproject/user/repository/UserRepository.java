package com.mini_project.miniproject.user.repository;

import com.mini_project.miniproject.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByReferralCode(String referralCode);

    Optional<Users> findByEmail(String email);
}
