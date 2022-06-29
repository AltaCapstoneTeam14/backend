package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.*;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.*;
import com.alterra.capstone14.domain.reqbody.BankTransferBody;
import com.alterra.capstone14.domain.reqbody.GopayBody;
import com.alterra.capstone14.domain.resBody.BankChargeRes;
import com.alterra.capstone14.domain.resBody.GopayChargeRes;
import com.alterra.capstone14.repository.*;
import com.alterra.capstone14.util.Encryptor;
import com.alterra.capstone14.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    TopupProductRepository topupProductRepository;

    @Autowired
    PulsaProductRepository pulsaProductRepository;

    @Autowired
    TransactionDetailPulsaRepository transactionDetailPulsaRepository;

    @Autowired
    ModelMapper modelMapper;

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

    public ResponseEntity<Object> createTopupWithGopay(TransactionTopupDto transactionTopupDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<TopupProduct> topupProduct = topupProductRepository.findById(transactionTopupDto.getProductId());
        if(topupProduct.isEmpty()){
            Response.build(Response.notFound("product"), null, null, HttpStatus.BAD_REQUEST);
        }

        // create topup detail and add to db
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.GOPAY.value)
                .transferMethod(EPaymentType.GOPAY.value)
                .user(user.get())
                .productType(EProductType.TOPUP.value)
                .productId(transactionTopupDto.getProductId())
                .grossAmount(topupProduct.get().getGrossAmount())
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

    public ResponseEntity<Object> createTopupWithBankTranfer(TransactionTopupDto transactionTopupDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<TopupProduct> topupProduct = topupProductRepository.findById(transactionTopupDto.getProductId());
        if(topupProduct.isEmpty()){
            Response.build(Response.notFound("product"), null, null, HttpStatus.BAD_REQUEST);
        }

        // create topup detail and add to db
        // bank_transfer either bca, bni / bri
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.BANK_TRANSFER.value)
                .user(user.get())
                .productType(EProductType.TOPUP.value)
                .productId(transactionTopupDto.getProductId())
                .grossAmount(topupProduct.get().getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        List<String> bankTransferList = new ArrayList<>();
        for(EBankTransfer bankTransfer : EBankTransfer.values()) {
            bankTransferList.add(bankTransfer.value);
        }
        if(!bankTransferList.contains(transactionTopupDto.getTransferMethod())){
            return Response.build(Response.notFound("transfer_method"), null, null, HttpStatus.BAD_REQUEST);
        }
        transactionDetail.setTransferMethod(transactionTopupDto.getTransferMethod());
        transactionDetailRepository.save(transactionDetail);

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

    public ResponseEntity<Object> buyPulsa(TransactionPulsaDto buyPulsaDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<PulsaProduct> pulsaProduct = pulsaProductRepository.findById(buyPulsaDto.getProductId());
        if(pulsaProduct.isEmpty()){
            Response.build(Response.notFound("product pulsa"), null, null, HttpStatus.BAD_REQUEST);
        }

        if(pulsaProduct.isPresent() && pulsaProduct.get().getStock().compareTo(0L) <= 0){
            Response.build("Product is empty", null, null, HttpStatus.BAD_REQUEST);
        }

//        log.info("user balance {}", user.get().getBalance().getAmount());
//        log.info("gross amount {}", pulsaProduct.get().getGrossAmount());
//        log.info("compaere {}",user.get().getBalance().getAmount().compareTo(pulsaProduct.get().getGrossAmount()) );
        if(user.get().getBalance().getAmount().compareTo(pulsaProduct.get().getGrossAmount()) < 0 ){
            return Response.build("user balance not enough", null, null, HttpStatus.BAD_REQUEST);
        }

        user.get().getBalance().setAmount(user.get().getBalance().getAmount()-pulsaProduct.get().getGrossAmount());
        userRepository.save(user.get());

        pulsaProduct.get().setStock(pulsaProduct.get().getStock()-1);
        pulsaProductRepository.save(pulsaProduct.get());

        // create topup detail and add to db
        // bank_transfer either bca, bni / bri
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .user(user.get())
                .productType(EProductType.PULSA.value)
                .productId(buyPulsaDto.getProductId())
                .grossAmount(pulsaProduct.get().getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .build();

        transactionDetailRepository.save(transactionDetail);
        transactionDetail.setOrderId(orderIdPrefix + "-" + transactionDetail.getId());
        transactionDetailRepository.save(transactionDetail);

        TransactionDetailPulsa transactionDetailPulsa = TransactionDetailPulsa.builder()
                .phone(buyPulsaDto.getPhone())
                .status(EPulsaQuotaStatus.SUCCESS.value)
                .transactionDetail(transactionDetail)
                .build();

        transactionDetailPulsaRepository.save(transactionDetailPulsa);

        TransactionDetailDto transactionDetailDto = TransactionDetailDto.builder()
                .id(transactionDetail.getId())
                .orderId(transactionDetail.getOrderId())
                .status(transactionDetail.getStatus())
                .productType(transactionDetail.getProductType())
                .productId(transactionDetail.getProductId())
                .grossAmount(transactionDetail.getGrossAmount())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        return Response.build(Response.create("buy pulsa"), transactionDetailDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getNotification(String stringNotificationDto) throws JsonProcessingException {
        log.info("string notif {}", stringNotificationDto);
        NotificationDto notificationDto = objectMapper.readValue(stringNotificationDto, NotificationDto.class);

        String encryptedKey = Encryptor.encryptStringToSHA512(
                notificationDto.getOrderId() +
                        notificationDto.getStatusCode() +
                        notificationDto.getGrossAmount() +
                        midtransServerKey);

//        log.info("sign key {}", notificationDto.getSignatureKey());
//        log.info("encrypted key {}", encryptedKey);

        if(!notificationDto.getSignatureKey().equals(encryptedKey)){
            return Response.build("Not authorized", null, null, HttpStatus.BAD_REQUEST);
        }

        log.info("Transaction notification received. Order ID: {}. Transaction status: {}. Fraud status: {});",
                notificationDto.getOrderId(),
                notificationDto.getTransactionStatus(),
                notificationDto.getFraudStatus());

        Long transactionDetailId = Long.parseLong(notificationDto.getOrderId().split("-", 2)[1]);
//        log.info("transaction id {}", transactionDetailId);
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

        if(transactionDetail.get().getProductType().equals(EProductType.TOPUP.value)) {
            if(transactionDetail.get().getStatus().equals(ETransactionDBStatus.SUCCESS.value)){
                Optional<TopupProduct> productTopup = topupProductRepository.findById(transactionDetail.get().getProductId());
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
//        log.info("Product type : {}", transactionDetail.get().getProductType());

        return Response.build(Response.update("topup detail"), null, null, HttpStatus.OK);
    }
}
