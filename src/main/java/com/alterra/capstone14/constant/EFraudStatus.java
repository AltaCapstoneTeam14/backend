package com.alterra.capstone14.constant;

public enum EFraudStatus {
    ACCEPT("accept"),
    DENY("deny"),
    CHALLENGE("challenge");

    public final String value;

    EFraudStatus(String value) {
        this.value = value;
    }
}
