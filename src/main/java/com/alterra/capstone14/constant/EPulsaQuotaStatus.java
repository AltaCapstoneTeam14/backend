package com.alterra.capstone14.constant;

public enum EPulsaQuotaStatus {
    PENDING("pending"),
    SUCCESS("success"),
    FAILURE("failure");

    public final String value;

    EPulsaQuotaStatus(String value) {
        this.value = value;
    }
}
