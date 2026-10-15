package com.foodbridge.entity.enums;

public enum PickupStatus {
    SCHEDULED("Scheduled"),
    RESCHEDULED("Rescheduled"),
    IN_TRANSIT("In Transit"),
    COLLECTED("Collected by Recipient"),
    HANDED_OVER("Handed Over & Verified"),
    CANCELLED("Cancelled");

    private final String label;

    PickupStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
