package com.foodbridge.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CashContributionDto {

    @NotNull(message = "Campaign ID is required")
    private Long campaignId;

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(value = "10.0", message = "Contribution must be at least ₹10")
    private BigDecimal amount;

    @NotBlank(message = "Donor name is required")
    private String donorName;

    @NotBlank(message = "Donor email is required")
    @Email(message = "Please provide a valid email")
    private String donorEmail;

    private String donorMessage;

    private boolean demoConsent = true;

    public CashContributionDto() {
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public String getDonorMessage() {
        return donorMessage;
    }

    public void setDonorMessage(String donorMessage) {
        this.donorMessage = donorMessage;
    }

    public boolean isDemoConsent() {
        return demoConsent;
    }

    public void setDemoConsent(boolean demoConsent) {
        this.demoConsent = demoConsent;
    }
}
