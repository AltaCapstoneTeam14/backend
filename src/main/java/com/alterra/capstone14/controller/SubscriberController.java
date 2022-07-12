package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.service.SubscriberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/subscribers")
public class SubscriberController {
    @Autowired
    SubscriberService subscriberService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getSubscriberList() {
        return subscriberService.getSubscribers();
    }

    @PostMapping("")
    public ResponseEntity<Object> addSubscriber(@Valid @RequestBody SubscriberDto subscriberDto) throws JsonProcessingException {
        return subscriberService.addSubscriber(subscriberDto);
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteSubscriber(@PathVariable String email) {
        return subscriberService.deleteSubscriber(email);
    }
}
