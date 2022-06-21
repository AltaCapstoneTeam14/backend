package com.alterra.capstone14.constant;

public enum EPaymentType {
    BANK_TRANSFER("bank_transfer"),
    GOPAY("gopay");

    public final String value;

    EPaymentType(String value) {
        this.value = value;
    }
}
