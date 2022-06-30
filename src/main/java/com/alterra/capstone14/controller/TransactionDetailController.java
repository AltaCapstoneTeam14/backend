package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.TransactionPulsaDto;
import com.alterra.capstone14.domain.dto.TransactionDetailDto;
import com.alterra.capstone14.domain.dto.TransactionQuotaDto;
import com.alterra.capstone14.domain.dto.TransactionTopupDto;
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

    @PostMapping("/topup/gopay")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> transactionWithGopay(@RequestBody TransactionTopupDto transactionTopupDto) throws JsonProcessingException {
        return transactionDetail.createTopupWithGopay(transactionTopupDto);
    }

    @PostMapping("/topup/bank-transfer")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> topupWithBankTransfer(@RequestBody TransactionTopupDto transactionTopupDto) throws JsonProcessingException {
        return transactionDetail.createTopupWithBankTranfer(transactionTopupDto);
    }

    @PostMapping("/pulsa")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> buyPulsa(@RequestBody TransactionPulsaDto buyPulsaDto) throws JsonProcessingException {
        return transactionDetail.buyPulsa(buyPulsaDto);
    }

    @PostMapping("/quota")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> buyQuota(@RequestBody TransactionQuotaDto transactionQuotaDto) throws JsonProcessingException {
        return transactionDetail.buyQuota(transactionQuotaDto);
    }

    @PostMapping("/notification")
    public ResponseEntity<Object> midtransNotificationHandler(@RequestBody String notificationDto) throws JsonProcessingException {
        return transactionDetail.getNotification(notificationDto);
    }
}
