package com.foodbridge.entity.enums;

public enum FoodCategory {
    COOKED_MEALS("Cooked Meals & Curries"),
    BAKERY_BREAD("Bakery & Bread"),
    PACKAGED_FOOD("Packaged & Canned Food"),
    FRUITS_VEGETABLES("Fresh Fruits & Vegetables"),
    DAIRY("Dairy & Milk Products"),
    BEVERAGES("Beverages & Juices"),
    GRAINS_CEREALS("Rice, Grains & Cereals"),
    OTHER("Other Food Items");

    private final String label;

    FoodCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
