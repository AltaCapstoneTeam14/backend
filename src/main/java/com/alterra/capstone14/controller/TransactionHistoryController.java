package com.alterra.capstone14.controller;

import com.alterra.capstone14.service.TransactionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/transaction-history")
@Slf4j
public class TransactionHistoryController {

    @Autowired
    TransactionHistoryService transactionHistoryService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getProfile() {
        return transactionHistoryService.getTransactionHistory();
    }

}
