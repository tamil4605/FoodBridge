package com.foodbridge.entity.enums;

public enum AccountType {
    INDIVIDUAL_DONOR("Individual Donor"),
    BUSINESS_DONOR("Restaurant / Business"),
    RECIPIENT_ORG("NGO / Orphanage / Recipient"),
    ADMIN("Administrator");

    private final String label;

    AccountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
