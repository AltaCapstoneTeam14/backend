package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.*;
import com.alterra.capstone14.domain.common.ApiResponse;
import com.alterra.capstone14.domain.dao.*;
import com.alterra.capstone14.domain.dto.*;
import com.alterra.capstone14.domain.thirdparty.req.GopayBody;
import com.alterra.capstone14.domain.thirdparty.res.BankChargeRes;
import com.alterra.capstone14.domain.thirdparty.res.GopayAction;
import com.alterra.capstone14.domain.thirdparty.res.GopayChargeRes;
import com.alterra.capstone14.domain.thirdparty.res.VANumber;
import com.alterra.capstone14.repository.*;
import com.alterra.capstone14.util.Encryptor;
import com.alterra.capstone14.util.RandomString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TransactionDetailService.class)
public class TransactionDetailServiceTest {
    @MockBean
    TransactionDetailRepository transactionDetailRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ObjectMapper objectMapper;

    @MockBean
    BalanceRepository balanceRepository;

    @MockBean
    CoinRepository coinRepository;

    @MockBean
    TopupProductRepository topupProductRepository;

    @MockBean
    PulsaProductRepository pulsaProductRepository;

    @MockBean
    TransactionDetailPulsaRepository transactionDetailPulsaRepository;

    @MockBean
    QuotaProductRepository quotaProductRepository;

    @MockBean
    TransactionDetailQuotaRepository transactionDetailQuotaRepository;

    @MockBean
    TransactionDetailTopupRepository transactionDetailTopupRepository;

    @MockBean
    CashoutProductRepository cashoutProductRepository;

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

    @MockBean
    RandomString randomString;

    @MockBean
    WebClient webClient;

    @MockBean
    Encryptor encryptor;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    @Autowired
    TransactionDetailService transactionDetailService;

    @Test
    void createTopupWithGopaySuccess_Test() throws JsonProcessingException {

        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .balance(Balance.builder().id(1L).amount(0L).build())
                .coin(Coin.builder().id(1L).amount(0L).build())
                .build();

        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .name("Topup 100K")
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        String orderId = "T-dkdfj9sd09fD";

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.GOPAY.value)
                .transferMethod(EPaymentType.GOPAY.value)
                .user(user)
                .productType(EProductType.TOPUP.value)
                .productId(topupProduct.getId())
                .orderId(orderId)
                .grossAmount(topupProduct.getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        GopayBody gopayBody = GopayBody.builder()
                .paymentType(transactionDetail.getPaymentType())
                .orderId(transactionDetail.getOrderId())
                .grossAmount(transactionDetail.getGrossAmount())
                .build();

        String webClientResponse = "{\"status_code\":\"201\",\"status_message\":\"GoPay transaction is created\",\"transaction_id\":\"1c81f7a7-73f0-445a-9518-6b0c2b76f4dd\",\"order_id\":\"t2-rXawT4q6ttLC\",\"merchant_id\":\"G424936884\",\"gross_amount\":\"451000.00\",\"currency\":\"IDR\",\"payment_type\":\"gopay\",\"transaction_time\":\"2022-07-08 14:17:55\",\"transaction_status\":\"pending\",\"fraud_status\":\"accept\",\"actions\":[{\"name\":\"generate-qr-code\",\"method\":\"GET\",\"url\":\"https://api.sandbox.midtrans.com/v2/gopay/1c81f7a7-73f0-445a-9518-6b0c2b76f4dd/qr-code\"},{\"name\":\"deeplink-redirect\",\"method\":\"GET\",\"url\":\"https://simulator.sandbox.midtrans.com/gopay/partner/app/payment-pin?id=e76f8c16-6dc1-49df-b48a-0853adfac505\"},{\"name\":\"get-status\",\"method\":\"GET\",\"url\":\"https://api.sandbox.midtrans.com/v2/1c81f7a7-73f0-445a-9518-6b0c2b76f4dd/status\"},{\"name\":\"cancel\",\"method\":\"POST\",\"url\":\"https://api.sandbox.midtrans.com/v2/1c81f7a7-73f0-445a-9518-6b0c2b76f4dd/cancel\"}]}";

        List<GopayAction> gopayActionList = new ArrayList<>();
        GopayAction gopayAction1 = GopayAction.builder()
                .name("deeplink-redirect")
                .method("GET")
                .url("https://simulator.sandbox.midtrans.com/gopay/partner/app/payment-pin?id=154208eb-9620-4bd6-889d-67d42cadd8c0")
                .build();
        gopayActionList.add(gopayAction1);

        GopayChargeRes gopayChargeRes = GopayChargeRes.builder()
                .orderId(orderId)
                .statusCode("201")
                .transactionStatus(ETransactionStatus.PENDING.value)
                .fraudStatus(EFraudStatus.ACCEPT.value)
                .actions(gopayActionList)
                .build();

        TransactionDetailTopup transactionDetailTopup = TransactionDetailTopup.builder()
                .id(1L)
                .jsonNotification(webClientResponse)
                .transactionDetail(transactionDetail)
                .build();

        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(topupProduct.getAmount())
                .jsonNotification(webClientResponse)
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryTopup.getId())
                .name(topupProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionStatus.PENDING.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(topupProductRepository.findById(any())).thenReturn(Optional.of(topupProduct));
        when(randomString.generate(any())).thenReturn(orderId);
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var responseSpecMock = mock(WebClient.ResponseSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        when(objectMapper.readValue(anyString(), eq(GopayChargeRes.class))).thenReturn(gopayChargeRes);
        when(transactionDetailTopupRepository.save(any())).thenReturn(transactionDetailTopup);
        when(transactionHistoryTopupRepository.save(any())).thenReturn(transactionHistoryTopup);
        when(transactionHistoryRepository.save(any())).thenReturn(transactionHistory);

        TransactionTopupDto transactionTopupDto = TransactionTopupDto.builder()
                .productId(1L)
                .transferMethod(EPaymentType.GOPAY.value)
                .build();

        ResponseEntity<Object> response = transactionDetailService.createTopupWithGopay(transactionTopupDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        GopayChargeDto data = (GopayChargeDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Create transaction gopay success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(orderId, data.getOrderId());
        assertEquals(ETransactionStatus.PENDING.value, data.getStatus());
        assertNotNull(data.getActions());
        assertEquals(1, data.getActions().size());
    }

    @Test
    void createTopupWithBankTransferSuccess_Test() throws JsonProcessingException {

        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .balance(Balance.builder().id(1L).amount(0L).build())
                .coin(Coin.builder().id(1L).amount(0L).build())
                .build();

        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .name("Topup 100K")
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        String orderId = "T-dkdfj9sd09fD";

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .paymentType(EPaymentType.BANK_TRANSFER.value)
                .transferMethod(EBankTransfer.BNI.value)
                .user(user)
                .productType(EProductType.TOPUP.value)
                .productId(topupProduct.getId())
                .orderId(orderId)
                .grossAmount(topupProduct.getGrossAmount())
                .status(ETransactionDBStatus.CREATED.value)
                .build();

        String webClientResponse = "{\"status_code\":\"201\",\"status_message\":\"Success, Bank Transfer transaction is created\",\"transaction_id\":\"3f51929e-8eb6-4e1c-bd13-1b6e7b751f1b\",\"order_id\":\"t2-7sA6A2r5udRV\",\"merchant_id\":\"G424936884\",\"gross_amount\":\"151000.00\",\"currency\":\"IDR\",\"payment_type\":\"bank_transfer\",\"transaction_time\":\"2022-07-09 08:49:45\",\"transaction_status\":\"pending\",\"va_numbers\":[{\"bank\":\"bni\",\"va_number\":\"9883688447108649\"}],\"fraud_status\":\"accept\"}";

        VANumber vaNumber = VANumber.builder().vaNumber("9883688447108649").bank(EBankTransfer.BNI.value).build();
        List<VANumber> vaNumberList = new ArrayList<>();
        vaNumberList.add(vaNumber);

        BankChargeRes bankChargeRes = BankChargeRes.builder()
                .orderId(orderId)
                .statusCode("201")
                .transactionStatus(ETransactionStatus.PENDING.value)
                .fraudStatus(EFraudStatus.ACCEPT.value)
                .vaNumberList(vaNumberList)
                .build();

        TransactionDetailTopup transactionDetailTopup = TransactionDetailTopup.builder()
                .id(1L)
                .jsonNotification(webClientResponse)
                .transactionDetail(transactionDetail)
                .build();

        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(topupProduct.getAmount())
                .jsonNotification(webClientResponse)
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryTopup.getId())
                .name(topupProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionStatus.PENDING.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(topupProductRepository.findById(any())).thenReturn(Optional.of(topupProduct));
        when(randomString.generate(any())).thenReturn(orderId);
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);

        var reqBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        var reqHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        var responseSpecMock = mock(WebClient.ResponseSpec.class);
        var monoMock = mock(Mono.class);

        when(webClient.post()).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.uri(anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.header(anyString(), anyString())).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.contentType(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.accept(MediaType.APPLICATION_JSON)).thenReturn(reqBodyUriSpecMock);
        when(reqBodyUriSpecMock.bodyValue(anyString())).thenReturn(reqHeadersSpecMock);
        when(reqHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(webClientResponse);

        when(objectMapper.readValue(anyString(), eq(BankChargeRes.class))).thenReturn(bankChargeRes);
        when(transactionDetailTopupRepository.save(any())).thenReturn(transactionDetailTopup);
        when(transactionHistoryTopupRepository.save(any())).thenReturn(transactionHistoryTopup);
        when(transactionHistoryRepository.save(any())).thenReturn(transactionHistory);

        TransactionTopupDto transactionTopupDto = TransactionTopupDto.builder()
                .productId(1L)
                .transferMethod(EBankTransfer.BNI.value)
                .build();

        ResponseEntity<Object> response = transactionDetailService.createTopupWithBankTranfer(transactionTopupDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        BankChargeDto data = (BankChargeDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Create transaction bank transfer success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(orderId, data.getOrderId());
        assertEquals(ETransactionStatus.PENDING.value, data.getStatus());
        assertEquals(EBankTransfer.BNI.value, data.getBank());
        assertEquals("9883688447108649", data.getVaNumber());
    }

    @Test
    void buyPulsaSuccess_Test() throws JsonProcessingException {
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .balance(Balance.builder().id(1L).amount(100000L).build())
                .coin(Coin.builder().id(1L).amount(100000L).build())
                .build();

        Provider provider = Provider.builder()
                .id(1L)
                .name("Telkomsel")
                .build();

        PulsaProduct pulsaProduct = PulsaProduct.builder()
                .id(1L)
                .provider(provider)
                .name("Pulsa 20K")
                .denom(20000L)
                .grossAmount(21000L)
                .stock(10L)
                .build();

        String orderId = "T-dkdfj9sd09fD";

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .id(1L)
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .user(user)
                .productType(EProductType.PULSA.value)
                .productId(pulsaProduct.getId())
                .orderId(orderId)
                .grossAmount(pulsaProduct.getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionDetailPulsa transactionDetailPulsa = TransactionDetailPulsa.builder()
                .id(1L)
                .transactionDetail(transactionDetail)
                .phone("089888999888")
                .build();

        TransactionHistoryPulsa transactionHistoryPulsa = TransactionHistoryPulsa.builder()
                .id(1L)
                .denom(pulsaProduct.getDenom())
                .phone(transactionDetailPulsa.getPhone())
                .provider(provider.getName())
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryPulsa.getId())
                .name(pulsaProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(pulsaProductRepository.findById(any())).thenReturn(Optional.of(pulsaProduct));
        when(userRepository.save(any())).thenReturn(user);
        when(pulsaProductRepository.save(any())).thenReturn(pulsaProduct);
        when(randomString.generate(any())).thenReturn(orderId);
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);
        when(transactionDetailPulsaRepository.save(any())).thenReturn(transactionDetailPulsa);
        when(transactionHistoryPulsaRepository.save(any())).thenReturn(transactionHistoryPulsa);
        when(transactionHistoryRepository.save(any())).thenReturn(transactionHistory);

        TransactionPulsaDto transactionPulsaDto = TransactionPulsaDto.builder()
                .productId(1L)
                .phone("089888999888")
                .build();

        ResponseEntity<Object> response = transactionDetailService.buyPulsa(transactionPulsaDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TransactionDetailWithCoinDto data = (TransactionDetailWithCoinDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Buy pulsa success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(orderId, data.getOrderId());
        assertEquals(ETransactionDBStatus.SUCCESS.value, data.getStatus());
        assertEquals(EProductType.PULSA.value, data.getProductType());
        assertEquals(1L, data.getProductId());
        assertEquals(21000L, data.getGrossAmount());
        assertEquals(21000L/500, data.getCoinEarned());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void buyQuotaSuccess_Test() throws JsonProcessingException {
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .balance(Balance.builder().id(1L).amount(100000L).build())
                .coin(Coin.builder().id(1L).amount(100000L).build())
                .build();

        Provider provider = Provider.builder()
                .id(1L)
                .name("Telkomsel")
                .build();

        QuotaProduct quotaProduct = QuotaProduct.builder()
                .id(1L)
                .provider(provider)
                .name("5GB")
                .description("Quota 5GB siang malam 1 bulan")
                .grossAmount(21000L)
                .stock(10L)
                .build();

        String orderId = "T-dkdfj9sd09fD";

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .id(1L)
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .user(user)
                .productType(EProductType.QUOTA.value)
                .productId(quotaProduct.getId())
                .orderId(orderId)
                .grossAmount(quotaProduct.getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionDetailQuota transactionDetailQuota = TransactionDetailQuota.builder()
                .id(1L)
                .transactionDetail(transactionDetail)
                .phone("089888999888")
                .build();

        TransactionHistoryQuota transactionHistoryQuota = TransactionHistoryQuota.builder()
                .id(1L)
                .description(quotaProduct.getDescription())
                .phone(transactionDetailQuota.getPhone())
                .provider(provider.getName())
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryQuota.getId())
                .name(quotaProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(quotaProductRepository.findById(any())).thenReturn(Optional.of(quotaProduct));
        when(userRepository.save(any())).thenReturn(user);
        when(quotaProductRepository.save(any())).thenReturn(quotaProduct);
        when(randomString.generate(any())).thenReturn(orderId);
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);
        when(transactionDetailQuotaRepository.save(any())).thenReturn(transactionDetailQuota);
        when(transactionHistoryQuotaRepository.save(any())).thenReturn(transactionHistoryQuota);
        when(transactionHistoryRepository.save(any())).thenReturn(transactionHistory);

        TransactionQuotaDto transactionQuotaDto = TransactionQuotaDto.builder()
                .productId(1L)
                .phone("089888999888")
                .build();

        ResponseEntity<Object> response = transactionDetailService.buyQuota(transactionQuotaDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TransactionDetailWithCoinDto data = (TransactionDetailWithCoinDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Buy quota success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(orderId, data.getOrderId());
        assertEquals(ETransactionDBStatus.SUCCESS.value, data.getStatus());
        assertEquals(EProductType.QUOTA.value, data.getProductType());
        assertEquals(1L, data.getProductId());
        assertEquals(21000L, data.getGrossAmount());
        assertEquals(21000L/500, data.getCoinEarned());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void cashoutCoinToBalanceSuccess_Test() throws JsonProcessingException {
        Role userRole = Role.builder().id(1L).name(ERole.USER).build();

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .balance(Balance.builder().id(1L).amount(100000L).build())
                .coin(Coin.builder().id(1L).amount(100000L).build())
                .build();

        Provider provider = Provider.builder()
                .id(1L)
                .name("Telkomsel")
                .build();

        CashoutProduct cashoutProduct = CashoutProduct.builder()
                .id(1L)
                .name("Cashout 20K")
                .coinAmount(20000L)
                .balanceAmount(20000L)
                .build();

        String orderId = "T-dkdfj9sd09fD";

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .id(1L)
                .paymentType(EPaymentType.BALANCE.value)
                .transferMethod(EPaymentType.BALANCE.value)
                .user(user)
                .productType(EProductType.CASHOUT.value)
                .productId(cashoutProduct.getId())
                .orderId(orderId)
                .grossAmount(cashoutProduct.getCoinAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .createdAt(LocalDateTime.now())
                .build();

        TransactionHistoryCashout transactionHistoryCashout = TransactionHistoryCashout.builder()
                .id(1L)
                .balanceAmount(cashoutProduct.getBalanceAmount())
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryCashout.getId())
                .name(cashoutProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionDBStatus.SUCCESS.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(cashoutProductRepository.findById(any())).thenReturn(Optional.of(cashoutProduct));
        when(userRepository.save(any())).thenReturn(user);
        when(cashoutProductRepository.save(any())).thenReturn(cashoutProduct);
        when(randomString.generate(any())).thenReturn(orderId);
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);
        when(transactionHistoryCashoutRepository.save(any())).thenReturn(transactionHistoryCashout);
        when(transactionHistoryRepository.save(any())).thenReturn(transactionHistory);

        TransactionCashoutDto transactionCashoutDto = TransactionCashoutDto.builder()
                .productId(1L)
                .build();

        ResponseEntity<Object> response = transactionDetailService.cashoutCoinToBalance(transactionCashoutDto);

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        TransactionDetailDto data = (TransactionDetailDto) Objects.requireNonNull(apiResponse).getData();

        assertEquals("Cashout coin to balance success", apiResponse.getMessage());
        assertEquals("201", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNotNull(apiResponse.getData());
        assertEquals(orderId, data.getOrderId());
        assertEquals(ETransactionDBStatus.SUCCESS.value, data.getStatus());
        assertEquals(EProductType.CASHOUT.value, data.getProductType());
        assertEquals(1L, data.getProductId());
        assertEquals(20000L, data.getGrossAmount());
        assertNotNull(data.getCreatedAt());
    }

    @Test
    void getNotificationSettlementBankTransfer_Test() throws JsonProcessingException {
        String orderId = "T-dkdfj9sd09fD";

        NotificationDto notificationDto = NotificationDto.builder()
                .statusCode(String.valueOf(HttpStatus.CREATED.value()))
                .orderId(orderId)
                .grossAmount("151000.00")
                .signatureKey("encryptedString")
                .transactionStatus(ETransactionStatus.SETTLEMENT.value)
                .fraudStatus(EFraudStatus.ACCEPT.value)
                .build();

        Role userRole = Role.builder().id(1L).name(ERole.USER).build();
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .id(1L)
                .name("Hamid")
                .email("hamid@gmail.com")
                .password("password")
                .phone("081999888999")
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        Balance balance = Balance.builder().id(1L).amount(0L).build();
        user.setBalance(balance);

        Coin coin = Coin.builder().id(1L).amount(0L).build();
        user.setCoin(coin);

        TopupProduct topupProduct = TopupProduct.builder()
                .id(1L)
                .name("Topup 100K")
                .amount(100000L)
                .grossAmount(101000L)
                .build();

        TransactionDetail transactionDetail = TransactionDetail.builder()
                .id(1L)
                .user(user)
                .orderId(orderId)
                .productType(EProductType.TOPUP.value)
                .productId(topupProduct.getId())
                .grossAmount(topupProduct.getGrossAmount())
                .paymentType(EPaymentType.BANK_TRANSFER.value)
                .transferMethod(EBankTransfer.BNI.value)
                .status(ETransactionDBStatus.SUCCESS.value)
                .build();

        TransactionDetailTopup transactionDetailTopup = TransactionDetailTopup.builder()
                .id(1L)
                .jsonNotification("StringNotif")
                .transactionDetail(transactionDetail)
                .build();

        TransactionHistoryTopup transactionHistoryTopup = TransactionHistoryTopup.builder()
                .amount(topupProduct.getAmount())
                .jsonNotification("String notif")
                .build();

        TransactionHistory transactionHistory = TransactionHistory.builder()
                .userId(user.getId())
                .orderId(transactionDetail.getOrderId())
                .transactionDetailId(transactionDetail.getId())
                .productType(transactionDetail.getProductType())
                .productHistoryId(transactionHistoryTopup.getId())
                .name(topupProduct.getName())
                .grossAmount(transactionDetail.getGrossAmount())
                .status(ETransactionStatus.PENDING.value)
                .paymentType(transactionDetail.getPaymentType())
                .transferMethod(transactionDetail.getTransferMethod())
                .createdAt(transactionDetail.getCreatedAt())
                .build();

        when(objectMapper.readValue(anyString(), eq(NotificationDto.class))).thenReturn(notificationDto);
        when(encryptor.encryptStringToSHA512(anyString())).thenReturn("encryptedString");
        when(transactionDetailRepository.findByOrderId(anyString())).thenReturn(Optional.ofNullable(transactionDetail));
        when(transactionDetailTopupRepository.findByTransactionDetailId(anyLong())).thenReturn(Optional.ofNullable(transactionDetailTopup));
        when(transactionDetailRepository.save(any())).thenReturn(transactionDetail);
        when(topupProductRepository.findById(anyLong())).thenReturn(Optional.ofNullable(topupProduct));
        when(balanceRepository.findByUserId(anyLong())).thenReturn(Optional.ofNullable(balance));
        when(balanceRepository.save(any())).thenReturn(balance);
        when(coinRepository.findByUserId(anyLong())).thenReturn(Optional.ofNullable(coin));
        when(coinRepository.save(any())).thenReturn(coin);
        when(transactionHistoryRepository.findByOrderId(any())).thenReturn(Optional.ofNullable(transactionHistory));

        String stringNotification = "{\"va_numbers\":[{\"va_number\":\"9883688447108649\",\"bank\":\"bni\"}],\"transaction_time\":\"2022-07-09 08:49:45\",\"transaction_status\":\"pending\",\"transaction_id\":\"3f51929e-8eb6-4e1c-bd13-1b6e7b751f1b\",\"status_message\":\"midtrans payment notification\",\"status_code\":\"201\",\"signature_key\":\"1637248767870ed6cf977363ec6c5c81d79cd98609b5d7d7c929ed7b91d77a07bc3ccc49288c7e637bf61aa07f73c42fe9de7aca24385a585e351e204e624919\",\"payment_type\":\"bank_transfer\",\"payment_amounts\":[],\"order_id\":\"t2-7sA6A2r5udRV\",\"merchant_id\":\"G424936884\",\"gross_amount\":\"151000.00\",\"fraud_status\":\"accept\",\"currency\":\"IDR\"}";
        ResponseEntity<Object> response = transactionDetailService.getNotification(stringNotification);
        ApiResponse apiResponse = (ApiResponse) response.getBody();

        assertEquals("Update transaction detail success", apiResponse.getMessage());
        assertEquals("200", apiResponse.getCode());
        assertNotNull(apiResponse.getTimestamp());
        assertNull(apiResponse.getErrors());
        assertNull(apiResponse.getData());
    }
}
