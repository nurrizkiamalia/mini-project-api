package com.mini_project.miniproject.user.service.impl;

import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.helpers.CloudinaryService;
import com.mini_project.miniproject.user.dto.ChangePasswordDto;
import com.mini_project.miniproject.user.dto.ProfileResponseDto;
import com.mini_project.miniproject.user.dto.ProfileSettingsDto;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Points;
import com.mini_project.miniproject.user.entity.ReferralDiscount;
import com.mini_project.miniproject.user.entity.Role;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.repository.PointsRepository;
import com.mini_project.miniproject.user.repository.ReferralDiscountRepository;
import com.mini_project.miniproject.user.repository.UserRepository;
import com.mini_project.miniproject.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ReferralDiscountRepository referralDiscountRepository;
    private final PointsRepository pointsRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           ReferralDiscountRepository referralDiscountRepository,
                           PointsRepository pointsRepository,
                           CloudinaryService cloudinaryService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.referralDiscountRepository = referralDiscountRepository;
        this.pointsRepository = pointsRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    @Transactional
    public Users register(RegisterRequestDto registerRequestDto) {
        // check if email already exists
        Optional<Users> existingUser = userRepository.findByEmail(registerRequestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Failed to register. Email already exists.");
        }

        // validate referral code if provided
        Optional<Users> referralUser = Optional.empty();
        if (registerRequestDto.getReferralCode() != null && !registerRequestDto.getReferralCode().isEmpty()) {
            referralUser = userRepository.findByReferralCode(registerRequestDto.getReferralCode());
            if (!referralUser.isPresent()) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Failed to register. Invalid referral code.");
            }
        }

        // Store user data
        Users newUser = new Users();
        newUser.setFirstName(registerRequestDto.getFirstName());
        newUser.setLastName(registerRequestDto.getLastName());
        newUser.setEmail(registerRequestDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        newUser.setRole(Role.fromString(registerRequestDto.getRole()));
        newUser.setReferralCode(generateReferralCode());


        // save the user to the repository
        Users savedUser = userRepository.save(newUser);

        // Give 10% referral discount for the registered user and 10000 points for the referrer
        if (referralUser.isPresent()) {
            giveReferralDiscount(savedUser);
            addPointsToReferralOwner(referralUser.get());
        }

        return savedUser;
    }

    @Override
    public ProfileResponseDto getCurrentUserProfile(Authentication authentication) {
        Users currentUser = getUserFromAuthentication(authentication);
        return mapToProfileResponseDto(currentUser);
    }

    @Override
    @Transactional
    public void updateCurrentUserProfile(Authentication authentication, ProfileSettingsDto profileSettingsDto) throws IOException {
        Users currentUser = getUserFromAuthentication(authentication);
        updateUserProfile(currentUser, profileSettingsDto);
    }

    @Override
    @Transactional
    public void changeCurrentUserPassword(Authentication authentication, ChangePasswordDto changePasswordDto) {
        Users currentUser = getUserFromAuthentication(authentication);
        changeUserPassword(currentUser, changePasswordDto);
    }


// helpers

    private Users getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ProfileResponseDto mapToProfileResponseDto(Users user) {
        ProfileResponseDto dto = new ProfileResponseDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setReferralCode(user.getReferralCode());
        dto.setAvatar(user.getAvatar());
        dto.setQuotes(user.getQuotes());
        dto.setRole(user.getRole().name());
        dto.setPoints(calculateTotalPoints(user.getId()));
        dto.setReferralDiscount(getReferralDiscount(user.getId()));
        return dto;
    }

    private String getReferralDiscount(Long userId) {
        var referralDiscount = referralDiscountRepository.findByUserIdAndExpiryDateAfter(userId, LocalDate.now());
        if (referralDiscount == null){
            return "this user does not have referral discount";
        }
        return referralDiscount.getName();
    }

    private void updateUserProfile(Users user, ProfileSettingsDto profileSettingsDto) throws IOException {
        user.setFirstName(profileSettingsDto.getFirstName());
        user.setLastName(profileSettingsDto.getLastName());
        user.setQuotes(profileSettingsDto.getQuotes());

        if (profileSettingsDto.getAvatar() != null && !profileSettingsDto.getAvatar().isEmpty()) {
            validateImage(profileSettingsDto.getAvatar());
            String avatarUrl = cloudinaryService.uploadImage(profileSettingsDto.getAvatar());
            user.setAvatar(avatarUrl);
        }

        userRepository.save(user);
    }

    private void changeUserPassword(Users user, ChangePasswordDto changePasswordDto) {
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }


    private void validateImage(MultipartFile avatar) {
        if (avatar.getSize() > 1_000_000) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Image size must not exceed 1MB");
        }

        String contentType = avatar.getContentType();
        if (contentType == null || !Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp").contains(contentType)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid image format. Allowed formats: jpg, jpeg, png, webp");
        }
    }

    private int calculateTotalPoints(Long userId) {
        List<Points> userPoints = pointsRepository.findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(userId, LocalDate.now());
        return userPoints.stream().mapToInt(Points::getAmount).sum();
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
