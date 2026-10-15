package com.foodbridge.entity.enums;

public enum NeedPriority {
    LOW("Low Priority"),
    MEDIUM("Medium Priority"),
    HIGH("High Priority"),
    URGENT("Urgent Need");

    private final String label;

    NeedPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
