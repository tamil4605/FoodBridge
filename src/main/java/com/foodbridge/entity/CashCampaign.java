package com.foodbridge.entity;

import com.foodbridge.entity.enums.ApprovalStatus;
import com.foodbridge.entity.enums.CampaignStatus;
import com.foodbridge.entity.enums.CauseCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_campaigns")
public class CashCampaign {

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
    private CauseCategory causeCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @DecimalMin("100.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal raisedAmount = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private LocalDate deadline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Column(length = 255)
    private String imagePath;

    @Column(columnDefinition = "TEXT")
    private String adminReviewNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CashCampaign() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.raisedAmount == null) {
            this.raisedAmount = BigDecimal.ZERO;
        }
        if (this.approvalStatus == null) {
            this.approvalStatus = ApprovalStatus.PENDING;
        }
        if (this.status == null) {
            this.status = CampaignStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        if (raisedAmount == null) {
            return 0;
        }
        int percent = raisedAmount.multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 0, java.math.RoundingMode.HALF_UP).intValue();
        return Math.min(percent, 100);
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

    public CauseCategory getCauseCategory() {
        return causeCategory;
    }

    public void setCauseCategory(CauseCategory causeCategory) {
        this.causeCategory = causeCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getRaisedAmount() {
        return raisedAmount;
    }

    public void setRaisedAmount(BigDecimal raisedAmount) {
        this.raisedAmount = raisedAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAdminReviewNotes() {
        return adminReviewNotes;
    }

    public void setAdminReviewNotes(String adminReviewNotes) {
        this.adminReviewNotes = adminReviewNotes;
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
