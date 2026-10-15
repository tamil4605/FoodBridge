package com.foodbridge.entity.enums;

public enum DietaryType {
    VEGETARIAN("Vegetarian (Veg)"),
    NON_VEGETARIAN("Non-Vegetarian (Non-Veg)"),
    VEGAN("Vegan");

    private final String label;

    DietaryType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
