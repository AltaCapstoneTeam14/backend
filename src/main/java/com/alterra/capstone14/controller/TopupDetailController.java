package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.NotificationDto;
import com.alterra.capstone14.domain.dto.TopupDetailDto;
import com.alterra.capstone14.service.TopupDetailService;
import com.alterra.capstone14.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/topup")
@Slf4j
public class TopupDetailController {
    @Autowired
    TopupDetailService topupDetailService;

    @PostMapping("/gopay")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> topupWithGopay(@RequestBody TopupDetailDto topupDetailDto) throws JsonProcessingException {
            return topupDetailService.createTopupWithGopay(topupDetailDto);
    }

    @PostMapping("/bank-transfer")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> topupWithBankTransfer(@RequestBody TopupDetailDto topupDetailDto) throws JsonProcessingException {
        return topupDetailService.createTopupWithBankTranfer(topupDetailDto);
    }

    @PostMapping("/notification")
    public ResponseEntity<Object> createAdmin(@RequestBody NotificationDto notificationDto) throws JsonProcessingException {
        return topupDetailService.getNotification(notificationDto);
    }
}
