package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.EmailDto;
import com.alterra.capstone14.domain.dto.UserDto;
import com.alterra.capstone14.service.SuperAdminService;
import com.alterra.capstone14.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequestMapping(value = "/v1/super-admin")
public class SuperAdminController {
    @Autowired
    SuperAdminService superAdminService;

    @PutMapping("/add-admin")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Object> createAdmin(@Valid @RequestBody EmailDto emailDto) {
        return superAdminService.addAdminRole(emailDto);
    }
}
