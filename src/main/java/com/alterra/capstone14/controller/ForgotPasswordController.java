package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.PasswordDto;
import com.alterra.capstone14.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/reset-password")
public class ForgotPasswordController {
    @Autowired
    ForgotPasswordService resetPasswordService;

    @PostMapping("/request")
    public ResponseEntity<Object> requestResetPassword(@Valid @RequestBody EmailDto emailDto) {
        return resetPasswordService.requestResetPassword(emailDto);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Object> confirmResetPassword(@RequestParam(value = "t") String token, @Valid @RequestBody PasswordDto passwordDto) {
        return resetPasswordService.confirmResetPassword(token, passwordDto);
    }
}
