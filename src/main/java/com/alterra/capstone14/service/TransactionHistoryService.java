package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EProductType;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.*;
import com.alterra.capstone14.repository.*;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionHistoryService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;


    @Autowired
    TransactionHistoryCashoutRepository transactionHistoryCashoutRepository;

    @Autowired
    TransactionHistoryTopupRepository transactionHistoryTopupRepository;

    @Autowired
    TransactionHistoryPulsaRepository transactionHistoryPulsaRepository;

    @Autowired
    TransactionHistoryQuotaRepository transactionHistoryQuotaRepository;

    public ResponseEntity<Object> getTransactionHistory(){
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findByUserIdSorted(user.get().getId());
        List<TransactionHistoryDto<Object>> transactionHistoryDtos = new ArrayList<>();

        transactionHistories.forEach(transactionHistory -> {
            TransactionHistoryDto<Object> transactionHistoryDto = TransactionHistoryDto.builder()
                    .id(transactionHistory.getId())
                    .userId(transactionHistory.getUserId())
                    .orderId(transactionHistory.getOrderId())
                    .transactionDetailId(transactionHistory.getTransactionDetailId())
                    .productType(transactionHistory.getProductType())
                    .productHistoryId(transactionHistory.getId())
                    .name(transactionHistory.getName())
                    .grossAmount(transactionHistory.getGrossAmount())
                    .status(transactionHistory.getStatus())
                    .paymentType(transactionHistory.getPaymentType())
                    .transferMethod(transactionHistory.getTransferMethod())
                    .createdAt(transactionHistory.getCreatedAt())
                    .build();

            if(transactionHistory.getProductType().equals(EProductType.TOPUP.value)){
                Optional<TransactionHistoryTopup> transactionHistoryTopup = transactionHistoryTopupRepository.findById(transactionHistory.getProductHistoryId());
                transactionHistoryDto.setProduct(TransactionHistoryTopupDto.builder()
                                .amount(transactionHistoryTopup.get().getAmount())
                                .build());
            }

            if(transactionHistory.getProductType().equals(EProductType.CASHOUT.value)){
                Optional<TransactionHistoryCashout> transactionHistoryCashout = transactionHistoryCashoutRepository.findById(transactionHistory.getProductHistoryId());
                transactionHistoryDto.setProduct(TransactionHistoryCashoutDto.builder()
                                .balanceAmount(transactionHistoryCashout.get().getBalanceAmount())
                                .build());
            }

            if(transactionHistory.getProductType().equals(EProductType.PULSA.value)){
                Optional<TransactionHistoryPulsa> transactionHistoryPulsa = transactionHistoryPulsaRepository.findById(transactionHistory.getProductHistoryId());
                transactionHistoryDto.setProduct(TransactionHistoryPulsaDto.builder()
                                .provider(transactionHistoryPulsa.get().getProvider())
                                .denom(transactionHistoryPulsa.get().getDenom())
                                .phone(transactionHistoryPulsa.get().getPhone())
                                .build());
            }


            if(transactionHistory.getProductType().equals(EProductType.QUOTA.value)){
                Optional<TransactionHistoryQuota> transactionHistoryQuota = transactionHistoryQuotaRepository.findById(transactionHistory.getProductHistoryId());
                transactionHistoryDto.setProduct(TransactionHistoryQuotaDto.builder()
                        .provider(transactionHistoryQuota.get().getProvider())
                        .description(transactionHistoryQuota.get().getDescription())
                        .phone(transactionHistoryQuota.get().getPhone())
                        .build());
            }

            transactionHistoryDtos.add(transactionHistoryDto);
        });

        return Response.build(Response.get("transaction history"), transactionHistoryDtos, null, HttpStatus.CREATED);
    }
}
