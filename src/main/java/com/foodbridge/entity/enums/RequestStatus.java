package com.foodbridge.entity.enums;

public enum RequestStatus {
    PENDING("Pending Review"),
    ACCEPTED("Request Accepted"),
    REJECTED("Request Rejected"),
    CANCELLED("Request Cancelled"),
    COMPLETED("Completed");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
