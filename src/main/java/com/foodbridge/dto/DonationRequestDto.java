package com.foodbridge.dto;

import com.foodbridge.entity.enums.DonationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class DonationRequestDto {

    @NotNull(message = "Donation ID is required")
    private Long donationId;

    @NotNull(message = "Donation type is required")
    private DonationType donationType;

    @NotNull(message = "Requested quantity is required")
    @DecimalMin(value = "0.1", message = "Requested quantity must be at least 0.1")
    private Double requestedQuantity;

    private String message;

    public DonationRequestDto() {
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public DonationType getDonationType() {
        return donationType;
    }

    public void setDonationType(DonationType donationType) {
        this.donationType = donationType;
    }

    public Double getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Double requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
