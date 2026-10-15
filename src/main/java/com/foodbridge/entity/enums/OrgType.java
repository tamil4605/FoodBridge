package com.foodbridge.entity.enums;

public enum OrgType {
    NGO("Non-Governmental Organization"),
    ORPHANAGE("Orphanage"),
    OLD_AGE_HOME("Old Age Home"),
    SHELTER_HOME("Shelter Home"),
    COMMUNITY_KITCHEN("Community Kitchen"),
    CHARITY("Charitable Trust"),
    OTHER("Other Organization");

    private final String label;

    OrgType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
