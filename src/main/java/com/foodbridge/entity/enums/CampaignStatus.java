package com.foodbridge.entity.enums;

public enum CampaignStatus {
    ACTIVE("Active"),
    COMPLETED("Target Completed"),
    CANCELLED("Cancelled"),
    EXPIRED("Expired");

    private final String label;

    CampaignStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
