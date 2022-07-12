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
import com.alterra.capstone14.util.RandomString;
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
import org.springframework.transaction.annotation.Transactional;
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
    CoinRepository coinRepository;

    @Autowired
    TopupProductRepository topupProductRepository;

    @Autowired
    PulsaProductRepository pulsaProductRepository;

    @Autowired
    TransactionDetailPulsaRepository transactionDetailPulsaRepository;

    @Autowired
    QuotaProductRepository quotaProductRepository;

    @Autowired
    TransactionDetailQuotaRepository transactionDetailQuotaRepository;

    @Autowired
    TransactionDetailTopupRepository transactionDetailTopupRepository;

    @Autowired
    CashoutProductRepository cashoutProductRepository;

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

    @Autowired
    RandomString randomString;

    @Autowired
    WebClient webClient;

    @Autowired
    Encryptor encryptor;

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
            return Response.build(Response.notFound("product"), null, null, HttpStatus.BAD_REQUEST);
        }

        // create topup detail and add to db
        String orderId = randomString.generate(orderIdPrefix);
        log.info(orderId);
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.GOPAY.value)
                .transferMethod(EPaymentType.GOPAY.value)
                .user(user.get())
                .productType(EProductType.TOPUP.value)
                .productId(transactionTopupDto.getProductId())
                .orderId(orderId)
                .grossAmount(topupProduct.get().getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        transactionDetailRepository.save(transactionDetail);

        GopayBody gopayBody = GopayBody.builder()
                .paymentType(transactionDetail.getPaymentType())
                .orderId(transactionDetail.getOrderId())
                .grossAmount(transactionDetail.getGrossAmount())
                .build();

        log.info("req body {}", gopayBody.toString());

        String response = webClient.post()
                .uri(midtransBaseUrl+"/v2/charge")
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gopayBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("response web client : {}", response);

        GopayChargeRes gopayChargeRes = objectMapper.readValue(response, GopayChargeRes.class);
        if(gopayChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            transactionDetail.setStatus(gopayChargeRes.getTransactionStatus());
            transactionDetailRepository.save(transactionDetail);
            transactionDetailTopupRepository.save(TransactionDetailTopup.builder()
                    .transactionDetail(transactionDetail)
                    .jsonNotification(response)
                    .build());
        }else {
            return Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        GopayChargeDto gopayChargeDto = GopayChargeDto.builder()
                .orderId(gopayChargeRes.getOrderId())
                .actions(gopayChargeRes.getActions())
                .status(transactionDetail.getStatus())
                .build();

        //create transaction history
        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(topupProduct.get().getAmount())
                .jsonNotification(response)
                .build();
        transactionHistoryTopupRepository.save(transactionHistoryTopup);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .userId(user.get().getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryTopup.getId())
                .name(topupProduct.get().getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(gopayChargeRes.getTransactionStatus())
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build());

        return Response.build(Response.create("transaction gopay"), gopayChargeDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> createTopupWithBankTranfer(TransactionTopupDto transactionTopupDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<TopupProduct> topupProduct = topupProductRepository.findById(transactionTopupDto.getProductId());
        if(topupProduct.isEmpty()){
            return Response.build(Response.notFound("product"), null, null, HttpStatus.BAD_REQUEST);
        }

        // create topup detail and add to db
        // bank_transfer either bca, bni / bri
        String orderId = randomString.generate(orderIdPrefix);
        log.info(orderId);
        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.BANK_TRANSFER.value)
                .user(user.get())
                .productType(EProductType.TOPUP.value)
                .productId(transactionTopupDto.getProductId())
                .orderId(orderId)
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
                .orderId(transactionDetail.getOrderId())
                .grossAmount(transactionDetail.getGrossAmount())
                .bank(transactionDetail.getTransferMethod())
                .build();

        log.info("req body {}", bankTransferBody.toString());

        String response = webClient.post()
                .uri(midtransBaseUrl + "/v2/charge")
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bankTransferBody.toString())
                .retrieve()
                .bodyToMono(String.class).block();

        log.info("response : {}", response);

        BankChargeRes bankChargeRes = objectMapper.readValue(response, BankChargeRes.class);
        if(bankChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            transactionDetail.setStatus(bankChargeRes.getTransactionStatus());
            transactionDetailRepository.save(transactionDetail);
            transactionDetailTopupRepository.save(TransactionDetailTopup.builder()
                    .transactionDetail(transactionDetail)
                    .jsonNotification(response)
                    .build());
        }else {
            return Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BankChargeDto bankChargeDto = BankChargeDto.builder()
                .orderId(bankChargeRes.getOrderId())
                .bank(bankChargeRes.getVaNumberList().get(0).getBank())
                .status(transactionDetail.getStatus())
                .vaNumber(bankChargeRes.getVaNumberList().get(0).getVaNumber())
                .build();

        //create transaction history
        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(topupProduct.get().getAmount())
                .jsonNotification(response)
                .build();
        transactionHistoryTopupRepository.save(transactionHistoryTopup);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .userId(user.get().getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryTopup.getId())
                .name(topupProduct.get().getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(bankChargeRes.getTransactionStatus())
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build());

        return Response.build(Response.create("transaction bank transfer"), bankChargeDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> buyPulsa(TransactionPulsaDto buyPulsaDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<PulsaProduct> pulsaProduct = pulsaProductRepository.findById(buyPulsaDto.getProductId());
        if(pulsaProduct.isEmpty()){
            return Response.build(Response.notFound("product pulsa"), null, null, HttpStatus.BAD_REQUEST);
        }

        if(pulsaProduct.get().getStock().compareTo(0L) <= 0){
            return Response.build("Product is empty", null, null, HttpStatus.BAD_REQUEST);
        }

        if(user.get().getBalance().getAmount().compareTo(pulsaProduct.get().getGrossAmount()) < 0 ){
            return Response.build("user balance not enough", null, null, HttpStatus.BAD_REQUEST);
        }

        Long coinEarned = (pulsaProduct.get().getGrossAmount()/500);

        user.get().getBalance().setAmount(user.get().getBalance().getAmount()-pulsaProduct.get().getGrossAmount());
        user.get().getCoin().setAmount(user.get().getCoin().getAmount()+coinEarned);
        userRepository.save(user.get());

        pulsaProduct.get().setStock(pulsaProduct.get().getStock()-1);
        pulsaProductRepository.save(pulsaProduct.get());

        String orderId = randomString.generate(orderIdPrefix);
        TransactionDetail transactionDetail = transactionDetailRepository.save(TransactionDetail.builder()
                        .paymentType(EPaymentType.BALANCE.value)
                        .transferMethod(EPaymentType.BALANCE.value)
                        .user(user.get())
                        .productType(EProductType.PULSA.value)
                        .productId(buyPulsaDto.getProductId())
                        .orderId(orderId)
                        .grossAmount(pulsaProduct.get().getGrossAmount())
                        .status(ETransactionDBStatus.SUCCESS.value)
                        .build());
//        transactionDetailRepository.save(transactionDetail);

        TransactionDetailPulsa transactionDetailPulsa = TransactionDetailPulsa.builder()
                .phone(buyPulsaDto.getPhone())
                .transactionDetail(transactionDetail)
                .build();
        transactionDetailPulsaRepository.save(transactionDetailPulsa);

        log.info(transactionDetail.getCreatedAt().toString());
        TransactionDetailWithCoinDto transactionDetailDto = TransactionDetailWithCoinDto.builder()
                .id(transactionDetail.getId())
                .orderId(transactionDetail.getOrderId())
                .status(transactionDetail.getStatus())
                .productType(transactionDetail.getProductType())
                .productId(transactionDetail.getProductId())
                .grossAmount(transactionDetail.getGrossAmount())
                .coinEarned(coinEarned)
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        //create transaction history
        TransactionHistoryPulsa transactionHistoryPulsa = TransactionHistoryPulsa.builder()
                .provider(pulsaProduct.get().getProvider().getName())
                .denom(pulsaProduct.get().getDenom())
                .phone(transactionDetailPulsa.getPhone())
                .build();
        transactionHistoryPulsaRepository.save(transactionHistoryPulsa);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .userId(user.get().getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryPulsa.getId())
                .name(pulsaProduct.get().getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(transactionDetail.getStatus())
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build());

        return Response.build("Buy pulsa success", transactionDetailDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> buyQuota(TransactionQuotaDto transactionQuotaDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<QuotaProduct> quotaProduct = quotaProductRepository.findById(transactionQuotaDto.getProductId());
        if(quotaProduct.isEmpty()){
            return Response.build(Response.notFound("product quota"), null, null, HttpStatus.BAD_REQUEST);
        }

        if(quotaProduct.get().getStock().compareTo(0L) <= 0){
            return Response.build("Product is empty", null, null, HttpStatus.BAD_REQUEST);
        }

        if(user.get().getBalance().getAmount().compareTo(quotaProduct.get().getGrossAmount()) < 0 ){
            return Response.build("user balance not enough", null, null, HttpStatus.BAD_REQUEST);
        }

        Long coinEarned = (quotaProduct.get().getGrossAmount()/500);

        user.get().getBalance().setAmount(user.get().getBalance().getAmount()-quotaProduct.get().getGrossAmount());
        user.get().getCoin().setAmount(user.get().getCoin().getAmount()+coinEarned);
        userRepository.save(user.get());

        quotaProduct.get().setStock(quotaProduct.get().getStock()-1);
        quotaProductRepository.save(quotaProduct.get());

        String orderId = randomString.generate(orderIdPrefix);
        TransactionDetail transactionDetail = transactionDetailRepository.save(TransactionDetail.builder()
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .user(user.get())
                .productType(EProductType.QUOTA.value)
                .productId(transactionQuotaDto.getProductId())
                .orderId(orderId)
                .grossAmount(quotaProduct.get().getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .build());

        TransactionDetailQuota transactionDetailQuota = TransactionDetailQuota.builder()
                .phone(transactionQuotaDto.getPhone())
                .transactionDetail(transactionDetail)
                .build();
        transactionDetailQuotaRepository.save(transactionDetailQuota);

        TransactionDetailWithCoinDto transactionDetailDto = TransactionDetailWithCoinDto.builder()
                .id(transactionDetail.getId())
                .orderId(transactionDetail.getOrderId())
                .status(transactionDetail.getStatus())
                .productType(transactionDetail.getProductType())
                .productId(transactionDetail.getProductId())
                .grossAmount(transactionDetail.getGrossAmount())
                .coinEarned(coinEarned)
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        //create transaction history
        TransactionHistoryQuota transactionHistoryQuota = TransactionHistoryQuota.builder()
                .provider(quotaProduct.get().getProvider().getName())
                .description(quotaProduct.get().getDescription())
                .phone(transactionDetailQuota.getPhone())
                .build();
        transactionHistoryQuotaRepository.save(transactionHistoryQuota);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .userId(user.get().getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryQuota.getId())
                .name(quotaProduct.get().getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(transactionDetail.getStatus())
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build());

        return Response.build("Buy quota success", transactionDetailDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> cashoutCoinToBalance(TransactionCashoutDto transactionCashoutDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        Optional<CashoutProduct> cashoutProduct = cashoutProductRepository.findById(transactionCashoutDto.getProductId());
        if(cashoutProduct.isEmpty()){
            return Response.build(Response.notFound("product cashout"), null, null, HttpStatus.BAD_REQUEST);
        }

        if(user.get().getCoin().getAmount().compareTo(cashoutProduct.get().getCoinAmount()) < 0 ){
            return Response.build("user coin not enough", null, null, HttpStatus.BAD_REQUEST);
        }

        user.get().getBalance().setAmount(user.get().getBalance().getAmount()+cashoutProduct.get().getBalanceAmount());
        user.get().getCoin().setAmount(user.get().getCoin().getAmount()-cashoutProduct.get().getCoinAmount());
        userRepository.save(user.get());

        String orderId = randomString.generate(orderIdPrefix);
        TransactionDetail transactionDetail = transactionDetailRepository.save(TransactionDetail.builder()
                .paymentType(EPaymentType.COIN.value)
                .transferMethod(EPaymentType.COIN.value)
                .user(user.get())
                .productType(EProductType.CASHOUT.value)
                .productId(transactionCashoutDto.getProductId())
                .orderId(orderId)
                .grossAmount(cashoutProduct.get().getCoinAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .build());

        TransactionDetailDto transactionDetailDto = TransactionDetailDto.builder()
                .id(transactionDetail.getId())
                .orderId(transactionDetail.getOrderId())
                .status(transactionDetail.getStatus())
                .productType(transactionDetail.getProductType())
                .productId(transactionDetail.getProductId())
                .grossAmount(transactionDetail.getGrossAmount())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        //create transaction history
        TransactionHistoryCashout transactionHistoryCashout = TransactionHistoryCashout.builder()
                .balanceAmount(cashoutProduct.get().getBalanceAmount())
                .build();
        transactionHistoryCashoutRepository.save(transactionHistoryCashout);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .userId(user.get().getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryCashout.getId())
                .name(cashoutProduct.get().getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(transactionDetail.getStatus())
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build());

        return Response.build("Cashout coin to balance success", transactionDetailDto, null, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Object> getNotification(String stringNotificationDto) throws JsonProcessingException {
//        log.info("string notif {}", stringNotificationDto);
        NotificationDto notificationDto = objectMapper.readValue(stringNotificationDto, NotificationDto.class);

        String encryptedKey = encryptor.encryptStringToSHA512(
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

        Optional<TransactionDetail> transactionDetail = transactionDetailRepository.findByOrderId(notificationDto.getOrderId());
        if(transactionDetail.isEmpty()){
            return Response.build(Response.notFound("transaction detail"), null, null, HttpStatus.BAD_REQUEST);
        }
//        log.info(transactionDetail.get().getStatus());
        Optional<TransactionDetailTopup> transactionDetailTopup = transactionDetailTopupRepository.findByTransactionDetailId(transactionDetail.get().getId());
        if(transactionDetailTopup.isEmpty()){
            return Response.build(Response.notFound("transaction detail topup"), null, null, HttpStatus.BAD_REQUEST);
        }
//        log.info(String.valueOf(transactionDetailTopup.get().getJsonNotification()));
        transactionDetailTopup.get().setJsonNotification(stringNotificationDto);
        transactionDetailTopupRepository.save(transactionDetailTopup.get());

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

        // update user balance and coin
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

            Optional<Coin> coin = coinRepository.findByUserId(transactionDetail.get().getUser().getId());
            if (coin.isEmpty()) {
                Coin newCoin = Coin.builder()
                        .user(transactionDetail.get().getUser())
                        .amount(productTopup.get().getGrossAmount()/500)
                        .build();
                coinRepository.save(newCoin);
                log.info("User coin : {}", newCoin.getAmount());
            } else {
                coin.get().setAmount(coin.get().getAmount() + (productTopup.get().getGrossAmount()/500));
                coinRepository.save(coin.get());
                log.info("User coin : {}", coin.get().getAmount());
            }
        }
        log.info("Transaction status : {}", transactionDetail.get().getStatus());

        //update transaction history
        Optional<TransactionHistory> transactionHistory = transactionHistoryRepository.findByOrderId(transactionDetail.get().getOrderId());
        transactionHistory.ifPresent(transactionHistory1 -> {
            transactionHistory1.setStatus(transactionDetail.get().getStatus());
        });

        return Response.build(Response.update("transaction detail"), null, null, HttpStatus.OK);
    }
}
