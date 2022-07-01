package com.alterra.capstone14.constant;

public enum EPaymentType {
    BANK_TRANSFER("bank_transfer"),
    BALANCE("balance"),
    GOPAY("gopay"),
    COIN("coin");

    public final String value;

    EPaymentType(String value) {
        this.value = value;
    }
}
