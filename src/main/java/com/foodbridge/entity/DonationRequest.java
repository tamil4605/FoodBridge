package com.foodbridge.entity;

import com.foodbridge.entity.enums.DonationType;
import com.foodbridge.entity.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_requests")
public class DonationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_org_id", nullable = false)
    private User requesterOrg;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DonationType donationType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_donation_id")
    private FoodDonation foodDonation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_donation_id")
    private MaterialDonation materialDonation;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false)
    private Double requestedQuantity;

    @Column(length = 50)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(length = 255)
    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public DonationRequest() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User getDonor() {
        if (foodDonation != null) return foodDonation.getDonor();
        if (materialDonation != null) return materialDonation.getDonor();
        return null;
    }

    public String getItemTitle() {
        if (foodDonation != null) return foodDonation.getFoodName();
        if (materialDonation != null) return materialDonation.getItemName();
        return "Unknown Donation";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRequesterOrg() {
        return requesterOrg;
    }

    public void setRequesterOrg(User requesterOrg) {
        this.requesterOrg = requesterOrg;
    }

    public DonationType getDonationType() {
        return donationType;
    }

    public void setDonationType(DonationType donationType) {
        this.donationType = donationType;
    }

    public FoodDonation getFoodDonation() {
        return foodDonation;
    }

    public void setFoodDonation(FoodDonation foodDonation) {
        this.foodDonation = foodDonation;
    }

    public MaterialDonation getMaterialDonation() {
        return materialDonation;
    }

    public void setMaterialDonation(MaterialDonation materialDonation) {
        this.materialDonation = materialDonation;
    }

    public Double getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Double requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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
