package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @PutMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> delete() {
        return userService.deleteUser();
    }
}
