package com.foodbridge;

import com.foodbridge.dto.UserRegisterDto;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.AccountType;
import com.foodbridge.entity.enums.Role;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.repository.UserRepository;
import com.foodbridge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserAuthenticationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterIndividualDonorSuccessfully() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Suresh Kumar");
        dto.setEmail("suresh@test.com");
        dto.setPassword("Secret@123");
        dto.setConfirmPassword("Secret@123");
        dto.setPhone("+91 98401 99887");
        dto.setAccountType(AccountType.INDIVIDUAL_DONOR);
        dto.setAddress("12 Anna Salai");
        dto.setCity("Chennai");
        dto.setState("Tamil Nadu");
        dto.setPincode("600002");

        User saved = userService.registerUser(dto);

        assertNotNull(saved.getId());
        assertEquals("suresh@test.com", saved.getEmail());
        assertEquals(Role.ROLE_INDIVIDUAL_DONOR, saved.getRole());
        assertTrue(passwordEncoder.matches("Secret@123", saved.getPassword()));
        assertNotEquals("Secret@123", saved.getPassword()); // Hash check
    }

    @Test
    void testPreventDuplicateEmailRegistration() {
        UserRegisterDto dto1 = new UserRegisterDto();
        dto1.setName("User One");
        dto1.setEmail("duplicate@test.com");
        dto1.setPassword("Secret@123");
        dto1.setConfirmPassword("Secret@123");
        dto1.setPhone("+91 98401 11111");
        dto1.setAccountType(AccountType.INDIVIDUAL_DONOR);
        dto1.setAddress("Street 1");
        dto1.setCity("Chennai");
        dto1.setState("Tamil Nadu");
        dto1.setPincode("600001");
        userService.registerUser(dto1);

        UserRegisterDto dto2 = new UserRegisterDto();
        dto2.setName("User Two");
        dto2.setEmail("duplicate@test.com");
        dto2.setPassword("Secret@123");
        dto2.setConfirmPassword("Secret@123");
        dto2.setPhone("+91 98401 22222");
        dto2.setAccountType(AccountType.INDIVIDUAL_DONOR);
        dto2.setAddress("Street 2");
        dto2.setCity("Chennai");
        dto2.setState("Tamil Nadu");
        dto2.setPincode("600001");

        assertThrows(BadRequestException.class, () -> userService.registerUser(dto2));
    }

    @Test
    void testPreventPublicAdminRegistration() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Hacker Admin");
        dto.setEmail("hacker@test.com");
        dto.setPassword("Admin@123");
        dto.setConfirmPassword("Admin@123");
        dto.setPhone("+91 98401 33333");
        dto.setAccountType(AccountType.ADMIN);
        dto.setAddress("HQ");
        dto.setCity("Chennai");
        dto.setState("Tamil Nadu");
        dto.setPincode("600001");

        assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
    }

    @Test
    void testPasswordMismatchThrowsException() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("Test User");
        dto.setEmail("mismatch@test.com");
        dto.setPassword("Password123");
        dto.setConfirmPassword("Different123");
        dto.setPhone("+91 98401 44444");
        dto.setAccountType(AccountType.INDIVIDUAL_DONOR);
        dto.setAddress("Street 3");
        dto.setCity("Chennai");
        dto.setState("Tamil Nadu");
        dto.setPincode("600001");

        assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
    }
}
