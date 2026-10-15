package com.foodbridge.dto;

import com.foodbridge.entity.enums.ItemCondition;
import com.foodbridge.entity.enums.MaterialCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class MaterialDonationDto {

    private Long id;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Category is required")
    private MaterialCategory category;

    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Condition is required")
    private ItemCondition conditionStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDate;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate preferredPickupDate;

    private String pickupInstructions;

    private String imagePath;

    public MaterialDonationDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public MaterialCategory getCategory() {
        return category;
    }

    public void setCategory(MaterialCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ItemCondition getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ItemCondition conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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

    public LocalDate getPreferredPickupDate() {
        return preferredPickupDate;
    }

    public void setPreferredPickupDate(LocalDate preferredPickupDate) {
        this.preferredPickupDate = preferredPickupDate;
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
