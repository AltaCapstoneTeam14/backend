package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.*;
import com.alterra.capstone14.domain.dao.Balance;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.BankChargeDto;
import com.alterra.capstone14.domain.dto.GopayChargeDto;
import com.alterra.capstone14.domain.dto.NotificationDto;
import com.alterra.capstone14.domain.reqbody.BankTransferBody;
import com.alterra.capstone14.domain.dao.TransactionDetail;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.TransactionDetailDto;
import com.alterra.capstone14.domain.reqbody.GopayBody;
import com.alterra.capstone14.domain.resBody.BankChargeRes;
import com.alterra.capstone14.domain.resBody.GopayChargeRes;
import com.alterra.capstone14.repository.BalanceRepository;
import com.alterra.capstone14.repository.TopupProductRepository;
import com.alterra.capstone14.repository.TransactionDetailRepository;
import com.alterra.capstone14.repository.UserRepository;
import com.alterra.capstone14.util.Encryptor;
import com.alterra.capstone14.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionDetailService {
    @Autowired
    TransactionDetailRepository transactionDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    TopupProductRepository productTopupRepository;

    @Value("${project.env:development}")
    private String projectEnv;

    WebClient webClient = WebClient.create();

    @Value("${midtrans.baseurl}")
    private String midtransBaseUrl;

    @Value("${midtrans.auth.header}")
    private String midtransAuthHeader;

    @Value("${midtrans.server.key}")
    private String midtransServerKey;

    @Value(("${orderid.prefix}"))
    private String orderIdPrefix;

    public ResponseEntity<Object> createTransactionWithGopay(TransactionDetailDto transactionDetailDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        // create topup detail and add to db
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType("gopay")
                .transferMethod("gopay")
                .user(user.get())
                .productType(transactionDetailDto.getProductType())
                .productId(transactionDetailDto.getProductId())
                .grossAmount(transactionDetailDto.getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        transactionDetailRepository.save(transactionDetail);

        GopayBody gopayBody = GopayBody.builder()
                .paymentType(transactionDetail.getPaymentType())
                .orderId(orderIdPrefix + "-" + transactionDetail.getId().toString())
                .grossAmount(transactionDetail.getGrossAmount())
                .build();

        log.info("req body {}", gopayBody.toString());

        String response = webClient.post()
                .uri(midtransBaseUrl+"/charge")
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gopayBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("response : {}", response);

        GopayChargeRes gopayChargeRes = new ObjectMapper().readValue(response, GopayChargeRes.class);
        if(gopayChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            transactionDetail.setStatus(gopayChargeRes.getTransactionStatus());
            transactionDetail.setOrderId(gopayChargeRes.getOrderId());
            transactionDetail.setJsonNotification(response);
            transactionDetailRepository.save(transactionDetail);
        }else {
            Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        GopayChargeDto gopayChargeDto = GopayChargeDto.builder()
                .orderId(gopayChargeRes.getOrderId())
                .actions(gopayChargeRes.getActions())
                .status(transactionDetail.getStatus())
                .build();

        return Response.build(Response.create("transaction gopay"), gopayChargeDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> createTransactionWithBankTranfer(TransactionDetailDto transactionDetailDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        // check product type and if product is ready
        if(transactionDetailDto.getProductType().equals(EProductType.TOPUP.value)){
            if(!productTopupRepository.existsById(transactionDetailDto.getProductId())){
                Response.build(Response.notFound("product"), null, null, HttpStatus.BAD_REQUEST);
            }
        }

        // create topup detail and add to db
        // bank_transfer either bca, bni / bri
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType("bank_transfer")
                .user(user.get())
                .productType(transactionDetailDto.getProductType())
                .productId(transactionDetailDto.getProductId())
                .grossAmount(transactionDetailDto.getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        List<String> bankTransferList = new ArrayList<>();
        for(EBankTransfer bankTransfer : EBankTransfer.values()) {
            bankTransferList.add(bankTransfer.value);
        }
        if(!bankTransferList.contains(transactionDetailDto.getTransferMethod())){
            return Response.build(Response.notFound("transfer_method"), null, null, HttpStatus.BAD_REQUEST);
        }
        transactionDetail.setTransferMethod(transactionDetailDto.getTransferMethod());

        transactionDetailRepository.save(transactionDetail);
//        transactionDetail.setOrderId(orderIdPrefix + "-" + transactionDetail.getOrderId());
//        transactionDetailRepository.save(transactionDetail);

        BankTransferBody bankTransferBody = BankTransferBody.builder()
                .paymentType(transactionDetail.getPaymentType())
                .orderId(orderIdPrefix + "-" + transactionDetail.getId().toString())
                .grossAmount(transactionDetail.getGrossAmount())
                .bank(transactionDetail.getTransferMethod())
                .build();

        log.info("req body {}", bankTransferBody.toString());

        String response = webClient.post()
                .uri(midtransBaseUrl + "/charge")
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bankTransferBody.toString())
                .retrieve()
                .bodyToMono(String.class).block();

        log.info("response : {}", response);

        BankChargeRes bankChargeRes = new ObjectMapper().readValue(response, BankChargeRes.class);
        if(bankChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            transactionDetail.setStatus(bankChargeRes.getTransactionStatus());
            transactionDetail.setOrderId(bankChargeRes.getOrderId());
            transactionDetail.setJsonNotification(response);
            transactionDetailRepository.save(transactionDetail);
        }else {
            Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BankChargeDto bankChargeDto = BankChargeDto.builder()
                .orderId(bankChargeRes.getOrderId())
                .bank(bankChargeRes.getVaNumberList().get(0).getBank())
                .status(transactionDetail.getStatus())
                .vaNumber(bankChargeRes.getVaNumberList().get(0).getVaNumber())
                .build();

        return Response.build(Response.create("transaction bank transfer"), bankChargeDto, null, HttpStatus.CREATED);
    }

//    public ResponseEntity<Object> getNotification(NotificationDto notificationDto) throws JsonProcessingException {
    public ResponseEntity<Object> getNotification(String stringNotificationDto) throws JsonProcessingException {
        log.info("string notif {}", stringNotificationDto);
        NotificationDto notificationDto = objectMapper.readValue(stringNotificationDto, NotificationDto.class);

        String encryptedKey = Encryptor.encryptStringToSHA512(
                notificationDto.getOrderId() +
                        notificationDto.getStatusCode() +
                        notificationDto.getGrossAmount() +
                        midtransServerKey);

        log.info("sign key {}", notificationDto.getSignatureKey());
        log.info("encrypted key {}", encryptedKey);

        if(!notificationDto.getSignatureKey().equals(encryptedKey)){
            return Response.build("Not authorized", null, null, HttpStatus.BAD_REQUEST);
        }

        log.info("Transaction notification received. Order ID: {}. Transaction status: {}. Fraud status: {});",
                notificationDto.getOrderId(),
                notificationDto.getTransactionStatus(),
                notificationDto.getFraudStatus());

        Long transactionDetailId = Long.parseLong(notificationDto.getOrderId().split("-", 2)[1]);
        log.info("transaction id {}", transactionDetailId);
        Optional<TransactionDetail> transactionDetail = transactionDetailRepository.findById(transactionDetailId);
        if(transactionDetail.isEmpty()){
            return Response.build(Response.notFound("order_id"), null, null, HttpStatus.BAD_REQUEST);
        }
        if(notificationDto.getTransactionStatus().equals(ETransactionStatus.CAPTURE.value)){
            if(notificationDto.getFraudStatus().equals(EFraudStatus.CHALLENGE.value)){
                transactionDetail.get().setStatus(ETransactionDBStatus.CHALLENGE.value);
                transactionDetailRepository.save(transactionDetail.get());
            }else if(notificationDto.getFraudStatus().equals(EFraudStatus.ACCEPT.value)){
                transactionDetail.get().setStatus(ETransactionDBStatus.SUCCESS.value);
                transactionDetailRepository.save(transactionDetail.get());
            }
        }else if(notificationDto.getTransactionStatus().equals(ETransactionStatus.SETTLEMENT.value)){
            transactionDetail.get().setStatus(ETransactionDBStatus.SUCCESS.value);
            transactionDetailRepository.save(transactionDetail.get());
        }else if(notificationDto.getTransactionStatus().equals(ETransactionStatus.CANCEL.value) ||
                notificationDto.getTransactionStatus().equals(ETransactionStatus.DENY.value) ||
                notificationDto.getTransactionStatus().equals(ETransactionStatus.EXPIRE.value)
        ){
            transactionDetail.get().setStatus(ETransactionDBStatus.FAILURE.value);
            transactionDetailRepository.save(transactionDetail.get());
        }else if(notificationDto.getTransactionStatus().equals(ETransactionStatus.PENDING.value)){
            transactionDetail.get().setStatus(ETransactionDBStatus.PENDING.value);
            transactionDetailRepository.save(transactionDetail.get());
        }

        if(transactionDetail.get().getStatus().equals(ETransactionDBStatus.SUCCESS.value)){
            if(transactionDetail.get().getProductType().equals(EProductType.TOPUP.value)) {
                Optional<TopupProduct> productTopup = productTopupRepository.findById(transactionDetail.get().getProductId());
                Optional<Balance> balance = balanceRepository.findByUserId(transactionDetail.get().getUser().getId());
                if (balance.isEmpty()) {
                    Balance newBalance = Balance.builder()
                            .user(transactionDetail.get().getUser())
                            .amount(productTopup.get().getAmount())
                            .build();
                    balanceRepository.save(newBalance);
                    log.info("User balance : {}", newBalance.getAmount());
                } else {
                    balance.get().setAmount(balance.get().getAmount() + productTopup.get().getAmount());
                    balanceRepository.save(balance.get());
                    log.info("User balance : {}", balance.get().getAmount());
                }
            }
        }

        log.info("Transaction status : {}", transactionDetail.get().getStatus());
        log.info("Product type : {}", transactionDetail.get().getProductType());

        return Response.build(Response.update("topup detail"), transactionDetail, null, HttpStatus.OK);
    }
}