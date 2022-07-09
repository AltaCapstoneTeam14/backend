package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.*;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dao.TransactionHistory;
import com.alterra.capstone14.domain.dto.TransactionHistoryDto;
import com.alterra.capstone14.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    public void getTransactionHistoryPulsa_Test() {
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
                .productType(EProductType.PULSA.value)
                .productHistoryId(1L)
                .name("Pulsa 5K")
                .grossAmount(6000L)
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(EPaymentType.GOPAY.value)
                .transferMethod(EPaymentType.GOPAY.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistoryPulsa transactionHistoryPulsa = TransactionHistoryPulsa.builder()
                .provider("Tri")
                .denom(10000L)
                .phone("089504215913")
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(transactionHistoryRepository.findByUserIdSorted(any())).thenReturn((List.of(transactionHistories)));
        when(transactionHistoryPulsaRepository.findById(any())).thenReturn(Optional.of(transactionHistoryPulsa));

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
            assertEquals(EProductType.PULSA.value, dataHistory.getProductType());
            assertEquals(1L, dataHistory.getProductHistoryId());
            assertEquals("Pulsa 5K", dataHistory.getName());
            assertEquals(6000L, dataHistory.getGrossAmount());
            assertEquals(ETransactionDBStatus.SUCCESS.value, dataHistory.getStatus());
            assertEquals(EPaymentType.GOPAY.value, dataHistory.getPaymentType());
            assertEquals(EPaymentType.GOPAY.value, dataHistory.getTransferMethod());
            assertNotNull(dataHistory.getCreatedAt());
        }
    }

    @Test
    void getTransactionHistoryTopup_Test() {
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
                .id(2L)
                .userId(1L)
                .orderId("1")
                .transactionDetailId(1L)
                .productType(EProductType.TOPUP.value)
                .productHistoryId(2L)
                .name("Topup 20K")
                .grossAmount(21000L)
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(EPaymentType.BANK_TRANSFER.value)
                .transferMethod(EBankTransfer.BNI.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(100000L)
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(transactionHistoryRepository.findByUserIdSorted(any())).thenReturn((List.of(transactionHistories)));
        when(transactionHistoryTopupRepository.findById(any())).thenReturn(Optional.of(transactionHistoryTopup));

        ResponseEntity<Object> response = transactionHistoryService.getTransactionHistory();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<TransactionHistoryDto> data = (List<TransactionHistoryDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get transaction history success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        for (TransactionHistoryDto dataHistory : data) {
            assertEquals(2L, dataHistory.getId());
            assertEquals(1L, dataHistory.getUserId());
            assertEquals("1", dataHistory.getOrderId());
            assertEquals(1L, dataHistory.getTransactionDetailId());
            assertEquals(EProductType.TOPUP.value, dataHistory.getProductType());
            assertEquals(2L, dataHistory.getProductHistoryId());
            assertEquals("Topup 20K", dataHistory.getName());
            assertEquals(21000L, dataHistory.getGrossAmount());
            assertEquals(ETransactionDBStatus.SUCCESS.value, dataHistory.getStatus());
            assertEquals(EPaymentType.BANK_TRANSFER.value, dataHistory.getPaymentType());
            assertEquals(EBankTransfer.BNI.value, dataHistory.getTransferMethod());
            assertNotNull(dataHistory.getCreatedAt());
        }
    }

    @Test
    void getTransactionHistoryQuota_Test() {
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
                .id(3L)
                .userId(1L)
                .orderId("1")
                .transactionDetailId(1L)
                .productType(EProductType.QUOTA.value)
                .productHistoryId(3L)
                .name("Quota 1GB")
                .grossAmount(10000L)
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistoryQuota transactionHistoryQuota = TransactionHistoryQuota.builder()
                .provider("Telkomsel")
                .description("Quota 1GB")
                .phone("08950415913")
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(transactionHistoryRepository.findByUserIdSorted(any())).thenReturn((List.of(transactionHistories)));
        when(transactionHistoryQuotaRepository.findById(any())).thenReturn(Optional.of(transactionHistoryQuota));

        ResponseEntity<Object> response = transactionHistoryService.getTransactionHistory();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<TransactionHistoryDto> data = (List<TransactionHistoryDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get transaction history success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        for (TransactionHistoryDto dataHistory : data) {
            assertEquals(3L, dataHistory.getId());
            assertEquals(1L, dataHistory.getUserId());
            assertEquals("1", dataHistory.getOrderId());
            assertEquals(1L, dataHistory.getTransactionDetailId());
            assertEquals(EProductType.QUOTA.value, dataHistory.getProductType());
            assertEquals(3L, dataHistory.getProductHistoryId());
            assertEquals("Quota 1GB", dataHistory.getName());
            assertEquals(10000L, dataHistory.getGrossAmount());
            assertEquals(ETransactionDBStatus.SUCCESS.value, dataHistory.getStatus());
            assertEquals(EPaymentType.BALANCE.value, dataHistory.getPaymentType());
            assertEquals(EPaymentType.BALANCE.value, dataHistory.getTransferMethod());
            assertNotNull(dataHistory.getCreatedAt());
        }
    }

    @Test
    void getTransactionHistoryCashout_Test() {
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
                .id(4L)
                .userId(1L)
                .orderId("1")
                .transactionDetailId(1L)
                .productType(EProductType.CASHOUT.value)
                .productHistoryId(4L)
                .name("Cashout 50K")
                .grossAmount(50000L)
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistoryCashout transactionHistoryCashout = TransactionHistoryCashout.builder()
                .balanceAmount(100000L)
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        when(transactionHistoryRepository.findByUserIdSorted(any())).thenReturn((List.of(transactionHistories)));
        when(transactionHistoryCashoutRepository.findById(any())).thenReturn(Optional.of(transactionHistoryCashout));

        ResponseEntity<Object> response = transactionHistoryService.getTransactionHistory();

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        List<TransactionHistoryDto> data = (List<TransactionHistoryDto>) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Get transaction history success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        for (TransactionHistoryDto dataHistory : data) {
            assertEquals(4L, dataHistory.getId());
            assertEquals(1L, dataHistory.getUserId());
            assertEquals("1", dataHistory.getOrderId());
            assertEquals(1L, dataHistory.getTransactionDetailId());
            assertEquals(EProductType.CASHOUT.value, dataHistory.getProductType());
            assertEquals(4L, dataHistory.getProductHistoryId());
            assertEquals("Cashout 50K", dataHistory.getName());
            assertEquals(50000L, dataHistory.getGrossAmount());
            assertEquals(ETransactionDBStatus.SUCCESS.value, dataHistory.getStatus());
            assertEquals(EPaymentType.BALANCE.value, dataHistory.getPaymentType());
            assertEquals(EPaymentType.BALANCE.value, dataHistory.getTransferMethod());
            assertNotNull(dataHistory.getCreatedAt());
        }
    }
}

