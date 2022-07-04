package com.alterra.capstone14.controller;

import com.alterra.capstone14.service.WeeklyLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/daily")
@Slf4j
public class WeeklyLoginController {
    @Autowired
    WeeklyLoginService weeklyLoginService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getStatus() {
        return weeklyLoginService.getStatus();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getBonus() {
        return weeklyLoginService.getBonus();
    }
}
