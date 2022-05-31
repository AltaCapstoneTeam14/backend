package com.alterra.capstone14.domain.common;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {
    private LocalDateTime timestamp;
    private String code;
    private String message;
    private T data;
    private List<String> errors;
}