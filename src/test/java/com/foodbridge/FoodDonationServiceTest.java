package com.foodbridge;

import com.foodbridge.dto.FoodDonationDto;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.*;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.repository.FoodDonationRepository;
import com.foodbridge.repository.UserRepository;
import com.foodbridge.service.FoodDonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FoodDonationServiceTest {

    @Autowired
    private FoodDonationService foodDonationService;

    @Autowired
    private FoodDonationRepository foodDonationRepository;

    @Autowired
    private UserRepository userRepository;

    private User donor;

    @BeforeEach
    void setUp() {
        foodDonationRepository.deleteAll();
        userRepository.deleteAll();

        donor = new User("Saravana Restaurant", "donor@test.com", "hash", "+91 98401 11223",
                AccountType.BUSINESS_DONOR, Role.ROLE_BUSINESS_DONOR, "45 Usman Road", "Chennai", "Tamil Nadu", "600017");
        donor = userRepository.save(donor);
    }

    @Test
    void testCreateFoodDonationValid() {
        FoodDonationDto dto = new FoodDonationDto();
        dto.setFoodName("Fresh Vegetable Pulav");
        dto.setCategory(FoodCategory.COOKED_MEALS);
        dto.setDietaryType(DietaryType.VEGETARIAN);
        dto.setDescription("Cleanly cooked vegetable pulav packed in stainless trays.");
        dto.setQuantity(50.0);
        dto.setUnit("meals");
        dto.setPreparationDateTime(LocalDateTime.now().minusHours(1));
        dto.setSafeCollectionDeadline(LocalDateTime.now().plusHours(8));
        dto.setPickupAddress("45 Usman Road, T. Nagar");
        dto.setCity("Chennai");
        dto.setPincode("600017");

        FoodDonation created = foodDonationService.createFoodDonation(donor, dto);

        assertNotNull(created.getId());
        assertEquals("Fresh Vegetable Pulav", created.getFoodName());
        assertEquals(50.0, created.getQuantity());
        assertEquals(50.0, created.getRemainingQuantity());
        assertEquals(DonationStatus.AVAILABLE, created.getStatus());
        assertEquals(donor.getId(), created.getDonor().getId());
    }

    @Test
    void testRejectPastDeadlineFoodCreation() {
        FoodDonationDto dto = new FoodDonationDto();
        dto.setFoodName("Past Deadline Meal");
        dto.setCategory(FoodCategory.COOKED_MEALS);
        dto.setDietaryType(DietaryType.VEGETARIAN);
        dto.setQuantity(20.0);
        dto.setUnit("meals");
        dto.setPreparationDateTime(LocalDateTime.now().minusHours(5));
        dto.setSafeCollectionDeadline(LocalDateTime.now().minusHours(1)); // PAST DEADLINE!
        dto.setPickupAddress("Address");
        dto.setCity("Chennai");
        dto.setPincode("600017");

        assertThrows(BadRequestException.class, () -> foodDonationService.createFoodDonation(donor, dto));
    }

    @Test
    void testWithdrawFoodListing() {
        FoodDonationDto dto = new FoodDonationDto();
        dto.setFoodName("Lunch Trays");
        dto.setCategory(FoodCategory.COOKED_MEALS);
        dto.setDietaryType(DietaryType.VEGETARIAN);
        dto.setQuantity(30.0);
        dto.setUnit("meals");
        dto.setPreparationDateTime(LocalDateTime.now().minusHours(1));
        dto.setSafeCollectionDeadline(LocalDateTime.now().plusHours(6));
        dto.setPickupAddress("Address");
        dto.setCity("Chennai");
        dto.setPincode("600017");

        FoodDonation created = foodDonationService.createFoodDonation(donor, dto);
        foodDonationService.withdrawFoodDonation(created.getId(), donor);

        FoodDonation updated = foodDonationService.getFoodDonationById(created.getId());
        assertEquals(DonationStatus.WITHDRAWN, updated.getStatus());
    }
}
