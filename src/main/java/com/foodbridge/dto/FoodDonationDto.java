package com.foodbridge.dto;

import com.foodbridge.entity.enums.DietaryType;
import com.foodbridge.entity.enums.FoodCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class FoodDonationDto {

    private Long id;

    @NotBlank(message = "Food name is required")
    private String foodName;

    @NotNull(message = "Category is required")
    private FoodCategory category;

    private String description;

    @NotNull(message = "Dietary type is required")
    private DietaryType dietaryType;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.5", message = "Quantity must be at least 0.5")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Preparation time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime preparationDateTime;

    @NotNull(message = "Safe collection deadline is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime safeCollectionDeadline;

    private String storageInstructions;

    private String allergenInfo;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private String pickupInstructions;

    private String imagePath;

    public FoodDonationDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(DietaryType dietaryType) {
        this.dietaryType = dietaryType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getPreparationDateTime() {
        return preparationDateTime;
    }

    public void setPreparationDateTime(LocalDateTime preparationDateTime) {
        this.preparationDateTime = preparationDateTime;
    }

    public LocalDateTime getSafeCollectionDeadline() {
        return safeCollectionDeadline;
    }

    public void setSafeCollectionDeadline(LocalDateTime safeCollectionDeadline) {
        this.safeCollectionDeadline = safeCollectionDeadline;
    }

    public String getStorageInstructions() {
        return storageInstructions;
    }

    public void setStorageInstructions(String storageInstructions) {
        this.storageInstructions = storageInstructions;
    }

    public String getAllergenInfo() {
        return allergenInfo;
    }

    public void setAllergenInfo(String allergenInfo) {
        this.allergenInfo = allergenInfo;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
