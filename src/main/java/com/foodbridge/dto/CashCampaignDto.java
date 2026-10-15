package com.foodbridge.dto;

import com.foodbridge.entity.enums.CauseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CashCampaignDto {

    private Long id;

    @NotBlank(message = "Campaign title is required")
    private String title;

    @NotNull(message = "Cause category is required")
    private CauseCategory causeCategory;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "100.0", message = "Target amount must be at least ₹100")
    private BigDecimal targetAmount;

    @NotNull(message = "Campaign deadline is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    private String imagePath;

    public CashCampaignDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
