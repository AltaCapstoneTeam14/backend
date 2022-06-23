package com.alterra.capstone14.constant;

public enum EBankTransfer {
    BCA("bca"),
    BNI("bni"),
    BRI("bri");

    public final String value;

    EBankTransfer(String value) {
        this.value = value;
    }
}
