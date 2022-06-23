package com.alterra.capstone14.constant;

public enum EProductType {
    PULSA("pulsa"),
    QUOTA("quota"),
    TOPUP("topup");

    public final String value;

    EProductType(String value) {
        this.value = value;
    }
}
