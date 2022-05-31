package com.alterra.capstone14.controller;

import com.alterra.capstone14.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "")
    ResponseEntity<Object> getAll(){
        return userService.getUsers();
    }
}
