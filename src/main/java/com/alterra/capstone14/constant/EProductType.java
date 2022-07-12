package com.alterra.capstone14.constant;

public enum EProductType {
    PULSA("pulsa"),
    QUOTA("quota"),
    TOPUP("topup"),
    CASHOUT("cashout");

    public final String value;

    EProductType(String value) {
        this.value = value;
    }
}
