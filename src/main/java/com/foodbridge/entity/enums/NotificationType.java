package com.foodbridge.entity.enums;

public enum NotificationType {
    REQUEST_RECEIVED("New Request Received"),
    REQUEST_ACCEPTED("Request Accepted"),
    REQUEST_REJECTED("Request Declined"),
    PICKUP_SCHEDULED("Pickup Scheduled"),
    PICKUP_COLLECTED("Donation Collected"),
    PICKUP_COMPLETED("Handover Completed"),
    CAMPAIGN_UPDATE("Fundraising Campaign Update"),
    ORG_VERIFIED("Organization Verification Update"),
    FEEDBACK_REMINDER("Feedback Reminder"),
    GENERAL("Platform Announcement");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
