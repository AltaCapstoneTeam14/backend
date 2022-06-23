package com.alterra.capstone14.constant;

public enum ETransactionStatus {
    PENDING("pending"),
    CAPTURE("capture"),
    SETTLEMENT("settlement"),
    DENY("deny"),
    CANCEL("cancel"),
    EXPIRE("expire"),
    REFUND("refund"),
    PARTIAL_REFUND("partial_refund"),
    AUTHORIZE("authorize");

    public final String value;

    ETransactionStatus(String value) {
        this.value = value;
    }
}
