package com.alterra.capstone14.service;

import com.alterra.capstone14.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TransactionHistoryService.class)
public class TransactionHistoryServiceTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    TransactionHistoryRepository transactionHistoryRepository;


    @MockBean
    TransactionHistoryCashoutRepository transactionHistoryCashoutRepository;

    @MockBean
    TransactionHistoryTopupRepository transactionHistoryTopupRepository;

    @MockBean
    TransactionHistoryPulsaRepository transactionHistoryPulsaRepository;

    @MockBean
    TransactionHistoryQuotaRepository transactionHistoryQuotaRepository;

    @Autowired
    TransactionHistoryService transactionHistoryService;

    @Test
    void getTransactionHistorySuccess_Test(){

    }
}
