package com.foodbridge.entity.enums;

public enum MaterialCategory {
    CLOTHES_FOOTWEAR("Clothes & Footwear"),
    BLANKETS_BEDDING("Blankets & Bedding"),
    BOOKS_STATIONERY("Books & Study Materials"),
    SCHOOL_SUPPLIES("School Supplies & Bags"),
    GROCERIES_RATIONS("Groceries & Dry Rations"),
    HYGIENE_KITS("Hygiene & Sanitary Kits"),
    HOUSEHOLD_ESSENTIALS("Household Essentials & Utensils"),
    TOYS_CHILDCARE("Toys & Child Care Supplies"),
    FURNITURE("Furniture"),
    FUNCTIONAL_ELECTRONICS("Functional Electronics & Gadgets"),
    OTHER("Other Essential Materials");

    private final String label;

    MaterialCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
