package com.foodbridge.entity.enums;

public enum OrgVerificationStatus {
    PENDING("Verification Pending"),
    VERIFIED("Verified Organization"),
    REJECTED("Verification Rejected");

    private final String label;

    OrgVerificationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
