package com.foodbridge.entity.enums;

public enum NeedCategory {
    FOOD("Food & Prepared Meals"),
    CASH("Cash / Financial Support"),
    CLOTHES("Clothes & Garments"),
    BLANKETS("Blankets & Warm Bedding"),
    BOOKS("Books & Study Materials"),
    SCHOOL_SUPPLIES("School Supplies & Bags"),
    GROCERIES("Groceries & Provisions"),
    HYGIENE_KITS("Hygiene & Sanitary Kits"),
    OTHER("Other Community Essentials");

    private final String label;

    NeedCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
