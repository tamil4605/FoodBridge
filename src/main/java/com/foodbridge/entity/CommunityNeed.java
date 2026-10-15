package com.foodbridge.entity;

import com.foodbridge.entity.enums.NeedCategory;
import com.foodbridge.entity.enums.NeedPriority;
import com.foodbridge.entity.enums.NeedStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_needs")
public class CommunityNeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private User organization;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NeedCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @DecimalMin("0.1")
    @Column(nullable = false)
    private Double targetQuantity;

    @NotNull
    @Column(nullable = false)
    private Double fulfilledQuantity = 0.0;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String unit; // meals, kg, INR, blankets, kits, etc.

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NeedPriority priority = NeedPriority.MEDIUM;

    @NotNull
    @Column(nullable = false)
    private LocalDate targetDate;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String pincode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NeedStatus status = NeedStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CommunityNeed() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.fulfilledQuantity == null) {
            this.fulfilledQuantity = 0.0;
        }
        if (this.status == null) {
            this.status = NeedStatus.OPEN;
        }
        if (this.priority == null) {
            this.priority = NeedPriority.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getProgressPercentage() {
        if (targetQuantity == null || targetQuantity <= 0) {
            return 0;
        }
        int percent = (int) Math.round((fulfilledQuantity / targetQuantity) * 100);
        return Math.min(percent, 100);
    }

    public Double getRemainingQuantity() {
        if (targetQuantity == null) return 0.0;
        double diff = targetQuantity - (fulfilledQuantity != null ? fulfilledQuantity : 0.0);
        return Math.max(diff, 0.0);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOrganization() {
        return organization;
    }

    public void setOrganization(User organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NeedCategory getCategory() {
        return category;
    }

    public void setCategory(NeedCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Double targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public Double getFulfilledQuantity() {
        return fulfilledQuantity;
    }

    public void setFulfilledQuantity(Double fulfilledQuantity) {
        this.fulfilledQuantity = fulfilledQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public NeedPriority getPriority() {
        return priority;
    }

    public void setPriority(NeedPriority priority) {
        this.priority = priority;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
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

    public NeedStatus getStatus() {
        return status;
    }

    public void setStatus(NeedStatus status) {
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
