package com.alterra.capstone14.util;


import com.alterra.capstone14.domain.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class Response {

    private Response(){}

    public static <T> ResponseEntity<Object> build(String responseMessage, T data, List<String> errors, HttpStatus httpStatus){
        return new ResponseEntity<>(buildResponse(String.valueOf(httpStatus.value()), responseMessage, data, errors), httpStatus);
    }

    static <T> ApiResponse<T> buildResponse(String responseCode, String responseMessage, T data, List<String> errors){
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .code(responseCode)
                .message(responseMessage)
                .data(data)
                .errors(errors)
                .build();
    }

    public static String get(String entity){
        return String.format("Get %s success", entity);
    }

    public static String add(String entity){
        return String.format("Add %s success", entity);
    }

    public static String create(String entity){
        return String.format("Create %s success", entity);
    }

    public static String update(String entity){
        return String.format("Update %s success", entity);
    }

    public static String delete(String entity){
        return String.format("Delete %s success", entity);
    }

    public static String notFound(String entity){
        return String.format("%s not found", entity);
    }

    public static String exist(String entity, String field, String data){
        return String.format("%s with %s %s exist", entity, field, data);
    }
}
