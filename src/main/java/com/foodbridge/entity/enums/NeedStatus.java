package com.foodbridge.entity.enums;

public enum NeedStatus {
    OPEN("Open for Donations"),
    IN_PROGRESS("In Progress"),
    FULFILLED("Fulfilled"),
    CLOSED("Closed");

    private final String label;

    NeedStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
