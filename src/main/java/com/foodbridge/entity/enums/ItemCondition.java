package com.foodbridge.entity.enums;

public enum ItemCondition {
    NEW("Brand New / Unused"),
    LIKE_NEW("Like New"),
    GOOD("Good / Gently Used"),
    USED("Functional / Fair");

    private final String label;

    ItemCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
