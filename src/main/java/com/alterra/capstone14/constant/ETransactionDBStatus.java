package com.alterra.capstone14.constant;

public enum ETransactionDBStatus {
    CREATED("created"),
    PENDING("pending"),
    CHALLENGE("challenge"),
    SUCCESS("success"),
    FAILURE("failure");

    public final String value;

    ETransactionDBStatus(String value) {
        this.value = value;
    }
}
