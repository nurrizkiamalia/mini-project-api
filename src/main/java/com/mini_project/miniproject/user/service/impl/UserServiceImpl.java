package com.mini_project.miniproject.user.service.impl;

import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Points;
import com.mini_project.miniproject.user.entity.ReferralDiscount;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.repository.PointsRepository;
import com.mini_project.miniproject.user.repository.ReferralDiscountRepository;
import com.mini_project.miniproject.user.repository.UserRepository;
import com.mini_project.miniproject.user.service.UserService;
import jakarta.transaction.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ReferralDiscountRepository referralDiscountRepository;
    private final PointsRepository pointsRepository;
//    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ReferralDiscountRepository referralDiscountRepository, PointsRepository pointsRepository) {
        this.userRepository = userRepository;
        this.referralDiscountRepository = referralDiscountRepository;
        this.pointsRepository = pointsRepository;
//        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public Users register(RegisterRequestDto registerRequestDto) {
        // check if email already exists
        Optional<Users> existingUser = userRepository.findByEmail(registerRequestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Failed to register. Email already exists.");
        }

        // validate referral code if provided
        Optional<Users> referralUser = Optional.empty();
        if (registerRequestDto.getReferralCode() != null && !registerRequestDto.getReferralCode().isEmpty()) {
            referralUser = userRepository.findByReferralCode(registerRequestDto.getReferralCode());
            if (!referralUser.isPresent()) {
                throw new RuntimeException("Failed to register. Invalid referral code.");
            }
        }

        // Store user data
        Users newUser = new Users();
        newUser.setFirstName(registerRequestDto.getFirstName());
        newUser.setLastName(registerRequestDto.getLastName());
        newUser.setEmail(registerRequestDto.getEmail());
//        newUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        newUser.setPassword(registerRequestDto.getPassword());
        newUser.setReferralCode(generateReferralCode());

        // save the user to the repository
        Users savedUser = userRepository.save(newUser);

        // Give 10% referral discount for the registered user
        if (referralUser.isPresent()) {
            giveReferralDiscount(savedUser);
            addPointsToReferralOwner(referralUser.get());
        }

        // Give 10% referral discount for the registered user
        // giveReferralDiscount(savedUser);
        // set the expiry date of the points to be 3 month from now

        // Give 10000 points to the referral code owner
            // get the referral_code owner based on the registered referral_code in the table users
            // add +10000 points to that owner
            // set the expiry date of the points to be 3 month from now

        return savedUser;
    }

    private void addPointsToReferralOwner(Users referralOwner) {
        Points points = new Points();
        points.setUserId(referralOwner.getId());
        points.setAmount(10000);
        points.setExpiryDate(LocalDate.now().plusMonths(3));
        pointsRepository.save(points);
    }

    private void giveReferralDiscount(Users savedUser) {
        ReferralDiscount referralDiscount = new ReferralDiscount();
        referralDiscount.setUserId(savedUser.getId());
        referralDiscount.setExpiryDate(LocalDate.now().plusMonths(3));
        referralDiscountRepository.save(referralDiscount);
    }

    private String generateReferralCode() {
        // Define the characters allowed in the referral code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder referralCode = new StringBuilder();

        // Generate an 8-character referral code
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            referralCode.append(characters.charAt(index));
        }

        return referralCode.toString();
    }


}
