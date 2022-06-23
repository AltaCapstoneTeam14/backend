package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.NotificationDto;
import com.alterra.capstone14.domain.dto.TransactionDetailDto;
import com.alterra.capstone14.service.TransactionDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/transactions")
@Slf4j
public class TransactionDetailController {
    @Autowired
    TransactionDetailService transactionDetail;

    @PostMapping("/gopay")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> transactionWithGopay(@RequestBody TransactionDetailDto transactionDetailDto) throws JsonProcessingException {
        return transactionDetail.createTransactionWithGopay(transactionDetailDto);
    }

    @PostMapping("/bank-transfer")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> transactionWithBankTransfer(@RequestBody TransactionDetailDto transactionDetailDto) throws JsonProcessingException {
        return transactionDetail.createTransactionWithBankTranfer(transactionDetailDto);
    }

    @PostMapping("/notification")
    public ResponseEntity<Object> midtransNotificationHandler(@RequestBody String notificationDto) throws JsonProcessingException {
        return transactionDetail.getNotification(notificationDto);
    }
}
