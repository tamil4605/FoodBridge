package com.foodbridge.entity.enums;

public enum DonationType {
    FOOD("Food Donation"),
    MATERIAL("Material Donation");

    private final String label;

    DonationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
