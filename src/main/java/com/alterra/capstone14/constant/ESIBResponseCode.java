package com.alterra.capstone14.constant;

public enum ESIBResponseCode {
    DUPLICATE_PARAMETER("duplicate_parameter"),
    DOCUMENT_NOT_FOUND("document_not_found");

    public final String value;

    ESIBResponseCode(String value) {
        this.value = value;
    }
}
