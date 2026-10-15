package com.foodbridge.service;

import com.foodbridge.dto.PasswordChangeDto;
import com.foodbridge.dto.UserProfileDto;
import com.foodbridge.dto.UserRegisterDto;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.AccountType;
import com.foodbridge.entity.enums.NotificationType;
import com.foodbridge.entity.enums.OrgVerificationStatus;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       NotificationService notificationService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public User registerUser(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with email " + dto.getEmail() + " already exists.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match.");
        }

        // Security restriction: Never allow public registration as ADMIN
        if (dto.getAccountType() == AccountType.ADMIN) {
            throw new BadRequestException("Administrative accounts cannot be registered publicly.");
        }

        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setAccountType(dto.getAccountType());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setPincode(dto.getPincode());

        // Assign Role and organization fields based on AccountType
        switch (dto.getAccountType()) {
            case BUSINESS_DONOR -> {
                user.setRole(Role.ROLE_BUSINESS_DONOR);
                user.setOrganizationName(dto.getOrganizationName());
                user.setOrganizationType(dto.getOrganizationType());
                user.setVerificationStatus(OrgVerificationStatus.VERIFIED);
            }
            case RECIPIENT_ORG -> {
                user.setRole(Role.ROLE_RECIPIENT_ORG);
                user.setOrganizationName(dto.getOrganizationName());
                user.setOrganizationType(dto.getOrganizationType());
                user.setVerificationStatus(OrgVerificationStatus.PENDING); // Needs admin review
            }
            default -> {
                user.setRole(Role.ROLE_INDIVIDUAL_DONOR);
                user.setVerificationStatus(OrgVerificationStatus.VERIFIED);
            }
        }

        User savedUser = userRepository.save(user);

        // Send Welcome notification
        String welcomeMsg = "Welcome to FoodBridge! Thank you for joining our community in reducing food waste and helping those in need.";
        if (savedUser.getAccountType() == AccountType.RECIPIENT_ORG) {
            welcomeMsg += " Your organization profile has been submitted for verification by our administrators.";
        }
        notificationService.sendNotification(savedUser, "Welcome to FoodBridge", welcomeMsg, NotificationType.GENERAL, "/dashboard");

        auditService.logAction(savedUser.getEmail(), "REGISTER", "User", savedUser.getId(), "Registered new account as " + savedUser.getRole());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateProfile(User user, UserProfileDto dto) {
        user.setName(dto.getName().trim());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setPincode(dto.getPincode());

        if (user.getAccountType() == AccountType.BUSINESS_DONOR || user.getAccountType() == AccountType.RECIPIENT_ORG) {
            if (dto.getOrganizationName() != null && !dto.getOrganizationName().isBlank()) {
                user.setOrganizationName(dto.getOrganizationName().trim());
            }
            if (dto.getOrganizationType() != null) {
                user.setOrganizationType(dto.getOrganizationType());
            }
        }

        User updated = userRepository.save(user);
        auditService.logAction(user.getEmail(), "UPDATE_PROFILE", "User", user.getId(), "Profile updated successfully");
        return updated;
    }

    public void changePassword(User user, PasswordChangeDto dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match.");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation do not match.");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        auditService.logAction(user.getEmail(), "CHANGE_PASSWORD", "User", user.getId(), "Password updated successfully");
    }

    public void verifyOrganization(Long orgUserId, OrgVerificationStatus status, String notes, String adminEmail) {
        User orgUser = getUserById(orgUserId);
        if (orgUser.getAccountType() != AccountType.RECIPIENT_ORG) {
            throw new BadRequestException("Only recipient organizations can be verified.");
        }

        orgUser.setVerificationStatus(status);
        orgUser.setVerificationNotes(notes);
        if (status == OrgVerificationStatus.VERIFIED) {
            orgUser.setVerifiedAt(LocalDateTime.now());
        }
        userRepository.save(orgUser);

        String msg = status == OrgVerificationStatus.VERIFIED
                ? "Congratulations! Your organization profile has been officially verified on FoodBridge."
                : "Your organization verification status was updated to: " + status.getLabel() + ". Notes: " + notes;

        notificationService.sendNotification(orgUser, "Organization Verification Update", msg, NotificationType.ORG_VERIFIED, "/dashboard");
        auditService.logAction(adminEmail, "VERIFY_ORG", "User", orgUserId, "Status updated to " + status + ". Notes: " + notes);
    }

    public void toggleUserActive(Long userId, String adminEmail) {
        User user = getUserById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
        auditService.logAction(adminEmail, "TOGGLE_USER_ACTIVE", "User", userId, "Active state set to: " + user.isActive());
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getOrganizationsByStatus(OrgVerificationStatus status) {
        return userRepository.findByVerificationStatus(status);
    }
}
