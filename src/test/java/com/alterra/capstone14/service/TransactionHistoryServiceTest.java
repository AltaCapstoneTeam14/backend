package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.ERole;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dao.TransactionHistory;
import com.alterra.capstone14.domain.dto.TransactionHistoryDto;
import com.alterra.capstone14.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TransactionHistoryService.class)
public class TransactionHistoryServiceTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TransactionHistoryRepository transactionHistoryRepository;

    @MockBean
    private TransactionHistoryCashoutRepository transactionHistoryCashoutRepository;

    @MockBean
    private TransactionHistoryTopupRepository transactionHistoryTopupRepository;

    @MockBean
    private TransactionHistoryPulsaRepository transactionHistoryPulsaRepository;

    @MockBean
    private TransactionHistoryQuotaRepository transactionHistoryQuotaRepository;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Test
    void getTransactionHistory_Test() {
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Satya")
                .email("satya@gmail.com")
                .password("password")
                .phone("089504215913")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistory transactionHistories = TransactionHistory.builder()
                .id(1L)
                .userId(1L)
                .orderId("1")
                .transactionDetailId(1L)
                .productType("PULSA")
                .productHistoryId(1L)
                .name("Pulsa 5K")
                .grossAmount(6000L)
                .status("pending")
                .paymentType("gopay")
                .transferMethod("gopay")
                .createdAt(LocalDateTime.now())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(transactionHistoryRepository.findByUserIdSorted(any())).thenReturn((List.of(transactionHistories)));

        ResponseEntity<Object> response = transactionHistoryService.getTransactionHistory();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<TransactionHistoryDto> data = (List<TransactionHistoryDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get transaction history success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        for (TransactionHistoryDto dataHistory : data) {
            assertEquals(1L, dataHistory.getId());
            assertEquals(1L, dataHistory.getUserId());
            assertEquals("1", dataHistory.getOrderId());
            assertEquals(1L, dataHistory.getTransactionDetailId());
            assertEquals("PULSA", dataHistory.getProductType());
            assertEquals(1L, dataHistory.getProductHistoryId());
            assertEquals("Pulsa 5K", dataHistory.getName());
            assertEquals(6000L, dataHistory.getGrossAmount());
            assertEquals("pending", dataHistory.getStatus());
            assertEquals("gopay", dataHistory.getPaymentType());
            assertEquals("gopay", dataHistory.getTransferMethod());
            assertNotNull(dataHistory.getCreatedAt());
        }
    }
}

