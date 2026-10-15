package com.foodbridge;

import com.foodbridge.dto.*;
import com.foodbridge.entity.*;
import com.foodbridge.entity.enums.*;
import com.foodbridge.repository.*;
import com.foodbridge.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DonationWorkflowTest {

    @Autowired
    private FoodDonationService foodDonationService;

    @Autowired
    private DonationRequestService donationRequestService;

    @Autowired
    private PickupService pickupService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodDonationRepository foodDonationRepository;

    private User donor;
    private User recipientOrg;

    @BeforeEach
    void setUp() {
        foodDonationRepository.deleteAll();
        userRepository.deleteAll();

        donor = new User("Saravana Restaurant", "donor@test.com", "hash", "+91 98401 11223",
                AccountType.BUSINESS_DONOR, Role.ROLE_BUSINESS_DONOR, "45 Usman Road", "Chennai", "Tamil Nadu", "600017");
        donor = userRepository.save(donor);

        recipientOrg = new User("Hope Orphanage", "hope@test.com", "hash", "+91 98402 22334",
                AccountType.RECIPIENT_ORG, Role.ROLE_RECIPIENT_ORG, "24 GST Road", "Chennai", "Tamil Nadu", "600045");
        recipientOrg = userRepository.save(recipientOrg);
    }

    @Test
    void testCompleteDonationLifecycle() {
        // Step 1: Donor creates food listing
        FoodDonationDto foodDto = new FoodDonationDto();
        foodDto.setFoodName("Vegetable Dum Biryani");
        foodDto.setCategory(FoodCategory.COOKED_MEALS);
        foodDto.setDietaryType(DietaryType.VEGETARIAN);
        foodDto.setQuantity(50.0);
        foodDto.setUnit("meals");
        foodDto.setPreparationDateTime(LocalDateTime.now().minusHours(2));
        foodDto.setSafeCollectionDeadline(LocalDateTime.now().plusHours(8));
        foodDto.setPickupAddress("45 Usman Road, T. Nagar");
        foodDto.setCity("Chennai");
        foodDto.setPincode("600017");

        FoodDonation food = foodDonationService.createFoodDonation(donor, foodDto);
        assertEquals(DonationStatus.AVAILABLE, food.getStatus());

        // Step 2: Organization requests 30 meals
        DonationRequestDto reqDto = new DonationRequestDto();
        reqDto.setDonationId(food.getId());
        reqDto.setDonationType(DonationType.FOOD);
        reqDto.setRequestedQuantity(30.0);
        reqDto.setMessage("For 30 children in shelter");

        DonationRequest request = donationRequestService.submitRequest(recipientOrg, reqDto);
        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertEquals(DonationStatus.REQUESTED, foodDonationService.getFoodDonationById(food.getId()).getStatus());

        // Step 3: Donor accepts request
        donationRequestService.acceptRequest(request.getId(), donor);
        assertEquals(RequestStatus.ACCEPTED, donationRequestService.getRequestById(request.getId()).getStatus());
        assertEquals(DonationStatus.ACCEPTED, foodDonationService.getFoodDonationById(food.getId()).getStatus());

        // Step 4: Schedule Pickup
        PickupScheduleDto pickupDto = new PickupScheduleDto();
        pickupDto.setRequestId(request.getId());
        pickupDto.setScheduledDateTime(LocalDateTime.now().plusHours(2));
        pickupDto.setPickupAddress("45 Usman Road, T. Nagar");
        pickupDto.setCity("Chennai");
        pickupDto.setPincode("600017");
        pickupDto.setSpecialInstructions("Insulated carrier ready");

        Pickup pickup = pickupService.schedulePickup(pickupDto, recipientOrg);
        assertEquals(PickupStatus.SCHEDULED, pickup.getStatus());
        assertEquals(DonationStatus.PICKUP_SCHEDULED, foodDonationService.getFoodDonationById(food.getId()).getStatus());

        // Step 5: Confirm Collection
        pickupService.confirmCollection(pickup.getId(), recipientOrg);
        Pickup collectedPickup = pickupService.getPickupById(pickup.getId());
        assertEquals(PickupStatus.COLLECTED, collectedPickup.getStatus());

        // Step 6: Confirm Handover
        pickupService.confirmHandover(pickup.getId(), donor);
        Pickup completedPickup = pickupService.getPickupById(pickup.getId());
        assertEquals(PickupStatus.HANDED_OVER, completedPickup.getStatus());

        FoodDonation completedFood = foodDonationService.getFoodDonationById(food.getId());
        assertEquals(DonationStatus.COMPLETED, completedFood.getStatus());
        assertEquals(20.0, completedFood.getRemainingQuantity()); // 50 - 30 = 20 remaining!

        // Step 7: Feedback Submission
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setPickupId(completedPickup.getId());
        feedbackDto.setRating(5);
        feedbackDto.setComment("Fresh, piping hot meals. Children enjoyed every bit!");

        Feedback feedback = feedbackService.submitFeedback(feedbackDto, recipientOrg);
        assertNotNull(feedback.getId());
        assertEquals(5, feedback.getRating());
        assertEquals(donor.getId(), feedback.getReceiver().getId());
    }
}
