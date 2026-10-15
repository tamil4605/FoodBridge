package com.foodbridge.entity.enums;

public enum ApprovalStatus {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String label;

    ApprovalStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
