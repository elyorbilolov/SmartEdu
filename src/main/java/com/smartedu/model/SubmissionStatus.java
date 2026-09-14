package com.smartedu.model;

public enum SubmissionStatus {
    SUBMITTED("Topshirilgan"),
    GRADED("Baholangan"),
    LATE("Kechikkan");

    private final String displayName;

    SubmissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
