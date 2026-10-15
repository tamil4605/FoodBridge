package com.foodbridge.entity;

import com.foodbridge.entity.enums.DietaryType;
import com.foodbridge.entity.enums.DonationStatus;
import com.foodbridge.entity.enums.FoodCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_donations")
public class FoodDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String foodName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private FoodCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DietaryType dietaryType;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false)
    private Double quantity;

    @NotNull
    @Column(nullable = false)
    private Double remainingQuantity;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String unit; // meals, portions, kg, packets, boxes

    @NotNull
    @Column(nullable = false)
    private LocalDateTime preparationDateTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime safeCollectionDeadline;

    @Column(length = 255)
    private String storageInstructions; // e.g. Keep refrigerated, Room temperature

    @Column(length = 255)
    private String allergenInfo; // e.g. Contains dairy, nuts, gluten

    @NotBlank
    @Column(nullable = false, length = 255)
    private String pickupAddress;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String pincode;

    @Column(length = 500)
    private String pickupInstructions;

    @Column(length = 255)
    private String imagePath;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DonationStatus status = DonationStatus.AVAILABLE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public FoodDonation() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.remainingQuantity == null) {
            this.remainingQuantity = this.quantity;
        }
        if (this.status == null) {
            this.status = DonationStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return safeCollectionDeadline != null && LocalDateTime.now().isAfter(safeCollectionDeadline);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDonor() {
        return donor;
    }

    public void setDonor(User donor) {
        this.donor = donor;
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

    public Double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
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

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
