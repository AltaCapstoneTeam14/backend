package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.LoginDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.service.AuthService;
import com.alterra.capstone14.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping(value = "/register")
    ResponseEntity<Object> register(@Valid @RequestBody UserDto userDto){
        return authService.registerUser(userDto);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDto loginDto) {
        return authService.loginUser(loginDto);
    }

    @PostMapping(value = "/login/admin")
    public ResponseEntity<Object> loginAdmin(@Valid @RequestBody LoginDto loginDto) {
        return authService.loginAdmin(loginDto);
    }

    @PostMapping(value = "/login/super-admin")
    public ResponseEntity<Object> loginSuperAdmin(@Valid @RequestBody LoginDto loginDto) {
        return authService.loginSuperAdmin(loginDto);
    }

}
