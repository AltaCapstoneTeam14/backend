package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.PasswordDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getProfile() {
        return userService.getProfile();
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> updateUserData(@Valid @RequestBody UserNoPwdDto userDto) {
        return userService.updateUserData(userDto);
    }

    @PutMapping("/password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> updateUserPassword(@Valid @RequestBody PasswordDto passwordDto) {
        return userService.updateUserPassword(passwordDto);
    }

    @DeleteMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> delete() {
        return userService.deleteUser();
    }

}
