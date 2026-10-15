package com.foodbridge.entity.enums;

public enum Role {
    ROLE_INDIVIDUAL_DONOR("Individual Donor"),
    ROLE_BUSINESS_DONOR("Restaurant / Business Donor"),
    ROLE_RECIPIENT_ORG("NGO / Recipient Organization"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
