package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.UserUpdateDto;
import com.alterra.capstone14.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getAll(@RequestParam int page, @RequestParam int size) {
        return adminService.getAllUser(page, size);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto, @PathVariable Long id) {
        return adminService.updateUser(userUpdateDto, id);
    }
}
