package com.foodbridge.entity.enums;

public enum DonationStatus {
    AVAILABLE("Available"),
    REQUESTED("Requested"),
    ACCEPTED("Accepted"),
    PICKUP_SCHEDULED("Pickup Scheduled"),
    COLLECTED("Collected"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    WITHDRAWN("Withdrawn"),
    EXPIRED("Expired");

    private final String label;

    DonationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
