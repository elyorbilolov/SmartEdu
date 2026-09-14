package com.smartedu.model;

public enum Role {
    ROLE_ADMIN("Administrator"),
    ROLE_TEACHER("O'qituvchi"),
    ROLE_STUDENT("Talaba");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
