package com.alterra.capstone14.constant;

public enum EWeeklyLoginStatus {
    CLAIMED("claimed"),
    NOT_CLAIMED("not_claimed");

    public final String value;

    EWeeklyLoginStatus(String value) {
        this.value = value;
    }
}
