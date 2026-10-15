package com.foodbridge.entity.enums;

public enum CauseCategory {
    FOOD_NUTRITION("Food & Nutrition"),
    EDUCATION("Education & Literacy"),
    CHILD_WELFARE("Child Welfare & Nutrition"),
    MEDICAL_ASSISTANCE("Medical Assistance & Healthcare"),
    EMERGENCY_RELIEF("Disaster & Emergency Relief"),
    SHELTER_HOUSING("Shelter & Housing"),
    COMMUNITY_DEVELOPMENT("Community Development"),
    GENERAL_NGO_SUPPORT("General NGO Support");

    private final String label;

    CauseCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
