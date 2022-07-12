package com.alterra.capstone14.constant;

public enum EResponseStatus {
    BAD_REQUEST("bad_request"),
    NOT_FOUND("not_found"),
    INTERNAL_SERVER_ERROR("internal_server_error"),
    SUCCESS("success");

    public final String value;

    EResponseStatus(String value) {
        this.value = value;
    }
}
