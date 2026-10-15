package com.foodbridge.service;

import com.foodbridge.dto.FoodDonationDto;
import com.foodbridge.entity.FoodDonation;
import com.foodbridge.entity.User;
import com.foodbridge.entity.enums.DietaryType;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.FoodCategory;
import com.foodbridge.exception.BadRequestException;
import com.foodbridge.exception.ResourceNotFoundException;
import com.foodbridge.exception.UnauthorizedException;
import com.foodbridge.repository.FoodDonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FoodDonationService {

    private final FoodDonationRepository foodDonationRepository;
    private final AuditService auditService;

    public FoodDonationService(FoodDonationRepository foodDonationRepository, AuditService auditService) {
        this.foodDonationRepository = foodDonationRepository;
        this.auditService = auditService;
    }

    public FoodDonation createFoodDonation(User donor, FoodDonationDto dto) {
        if (dto.getSafeCollectionDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Safe collection deadline must be in the future.");
        }
        if (dto.getPreparationDateTime().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Preparation time cannot be in the distant future.");
        }
        if (dto.getSafeCollectionDeadline().isBefore(dto.getPreparationDateTime())) {
            throw new BadRequestException("Safe collection deadline must be after the preparation date and time.");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        FoodDonation food = new FoodDonation();
        food.setDonor(donor);
        food.setFoodName(dto.getFoodName().trim());
        food.setCategory(dto.getCategory());
        food.setDescription(dto.getDescription());
        food.setDietaryType(dto.getDietaryType());
        food.setQuantity(dto.getQuantity());
        food.setRemainingQuantity(dto.getQuantity());
        food.setUnit(dto.getUnit().trim());
        food.setPreparationDateTime(dto.getPreparationDateTime());
        food.setSafeCollectionDeadline(dto.getSafeCollectionDeadline());
        food.setStorageInstructions(dto.getStorageInstructions());
        food.setAllergenInfo(dto.getAllergenInfo());
        food.setPickupAddress(dto.getPickupAddress().trim());
        food.setCity(dto.getCity().trim());
        food.setPincode(dto.getPincode().trim());
        food.setPickupInstructions(dto.getPickupInstructions());
        food.setImagePath(dto.getImagePath());
        food.setStatus(DonationStatus.AVAILABLE);

        FoodDonation saved = foodDonationRepository.save(food);
        auditService.logAction(donor.getEmail(), "CREATE_FOOD_DONATION", "FoodDonation", saved.getId(),
                "Created food listing: " + saved.getFoodName() + " (" + saved.getQuantity() + " " + saved.getUnit() + ")");
        return saved;
    }

    public FoodDonation updateFoodDonation(Long id, User donor, FoodDonationDto dto) {
        FoodDonation food = getFoodDonationById(id);
        if (!food.getDonor().getId().equals(donor.getId())) {
            throw new UnauthorizedException("You are not authorized to edit this food donation listing.");
        }
        if (food.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException("Only available food listings can be modified.");
        }
        if (dto.getSafeCollectionDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Safe collection deadline must be in the future.");
        }

        food.setFoodName(dto.getFoodName().trim());
        food.setCategory(dto.getCategory());
        food.setDescription(dto.getDescription());
        food.setDietaryType(dto.getDietaryType());
        food.setQuantity(dto.getQuantity());
        food.setRemainingQuantity(dto.getQuantity());
        food.setUnit(dto.getUnit().trim());
        food.setPreparationDateTime(dto.getPreparationDateTime());
        food.setSafeCollectionDeadline(dto.getSafeCollectionDeadline());
        food.setStorageInstructions(dto.getStorageInstructions());
        food.setAllergenInfo(dto.getAllergenInfo());
        food.setPickupAddress(dto.getPickupAddress().trim());
        food.setCity(dto.getCity().trim());
        food.setPincode(dto.getPincode().trim());
        food.setPickupInstructions(dto.getPickupInstructions());

        FoodDonation updated = foodDonationRepository.save(food);
        auditService.logAction(donor.getEmail(), "UPDATE_FOOD_DONATION", "FoodDonation", id, "Updated food listing");
        return updated;
    }

    public void withdrawFoodDonation(Long id, User donor) {
        FoodDonation food = getFoodDonationById(id);
        if (!food.getDonor().getId().equals(donor.getId())) {
            throw new UnauthorizedException("You can only withdraw your own food listings.");
        }
        if (food.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException("Cannot withdraw a listing that is not currently AVAILABLE.");
        }
        food.setStatus(DonationStatus.WITHDRAWN);
        foodDonationRepository.save(food);
        auditService.logAction(donor.getEmail(), "WITHDRAW_FOOD_DONATION", "FoodDonation", id, "Withdrew listing");
    }

    @Transactional(readOnly = true)
    public FoodDonation getFoodDonationById(Long id) {
        return foodDonationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food donation not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<FoodDonation> getAvailableFoodListings(FoodCategory category, DietaryType dietaryType,
                                                        String city, String pincode, String keyword) {
        expireOldListings();
        String catStr = category != null ? category.name() : null;
        String dietStr = dietaryType != null ? dietaryType.name() : null;
        String c = (city != null && !city.isBlank()) ? city.trim() : null;
        String p = (pincode != null && !pincode.isBlank()) ? pincode.trim() : null;
        String k = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return foodDonationRepository.searchAvailableFood(
                DonationStatus.AVAILABLE, LocalDateTime.now(), category, dietaryType, c, p, k);
    }

    @Transactional(readOnly = true)
    public List<FoodDonation> getFeaturedAvailableFood() {
        expireOldListings();
        return foodDonationRepository.findTop6ByStatusOrderByCreatedAtDesc(DonationStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public List<FoodDonation> getListingsByDonor(User donor) {
        expireOldListings();
        return foodDonationRepository.findByDonorOrderByCreatedAtDesc(donor);
    }

    public void expireOldListings() {
        List<FoodDonation> expired = foodDonationRepository.findExpiredDonations(LocalDateTime.now());
        for (FoodDonation f : expired) {
            f.setStatus(DonationStatus.EXPIRED);
        }
        if (!expired.isEmpty()) {
            foodDonationRepository.saveAll(expired);
        }
    }
}
