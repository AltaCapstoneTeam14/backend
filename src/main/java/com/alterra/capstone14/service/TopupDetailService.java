package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EBankTransfer;
import com.alterra.capstone14.constant.EPaymentType;
import com.alterra.capstone14.domain.dto.BankChargeDto;
import com.alterra.capstone14.domain.dto.GopayChargeDto;
import com.alterra.capstone14.domain.dto.NotificationDto;
import com.alterra.capstone14.domain.reqbody.BankTransferBody;
import com.alterra.capstone14.domain.reqbody.GopayBody;
import com.alterra.capstone14.domain.reqbody.TransactionDetailBody;
import com.alterra.capstone14.domain.dao.TopupDetail;
import com.alterra.capstone14.domain.dao.User;
import com.alterra.capstone14.domain.dto.TopupDetailDto;
import com.alterra.capstone14.domain.resBody.BankChargeRes;
import com.alterra.capstone14.domain.resBody.GopayAction;
import com.alterra.capstone14.domain.resBody.GopayChargeRes;
import com.alterra.capstone14.repository.TopupDetailRepository;
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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TopupDetailService {
    @Autowired
    TopupDetailRepository topupDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${midtrans.baseurl}")
    private String midtransBaseUrl;

    @Value("${midtrans.authHeader}")
    private String midtransAuthHeader;

    @Value("${midtrans.serverKey}")
    private String midtransServerKey;

    public ResponseEntity<Object> createTopupWithGopay(TopupDetailDto topupDetailDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        // create topup detail and add to db
        TopupDetail topupDetail = TopupDetail.builder()
                .user(user.get())
                .paymentType("gopay")
                .transferMethod("gopay")
                .amount(topupDetailDto.getAmount())
                .status("created")
                .grossAmount(topupDetailDto.getGrossAmount())
                .build();
        topupDetailRepository.save(topupDetail);

        GopayBody gopayBody = GopayBody.builder()
                .paymentType(topupDetail.getPaymentType())
                .orderId(topupDetail.getId().toString())
                .grossAmount(topupDetail.getGrossAmount())
                .build();

        log.info("req body {}", gopayBody.toString());

        WebClient client = WebClient.create();
        String response = client.post()
                .uri(midtransBaseUrl+"/charge")
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gopayBody.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("response {}", response);

        GopayChargeRes gopayChargeRes = new ObjectMapper().readValue(response, GopayChargeRes.class);
        if(gopayChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            topupDetail.setStatus("pending");
            topupDetailRepository.save(topupDetail);
        }else {
            Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        GopayChargeDto gopayChargeDto = GopayChargeDto.builder()
                .orderId(gopayChargeRes.getOrderId())
                .actions(gopayChargeRes.getActions())
                .status(topupDetail.getStatus())
                .build();

        return Response.build(Response.create("topup gopay"), gopayChargeDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> createTopupWithBankTranfer(TopupDetailDto topupDetailDto) throws JsonProcessingException {
        // get user logged in
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        // create topup detail and add to db
        // bank_transfer either bca, bni / bri
        TopupDetail topupDetail = TopupDetail.builder()
                .paymentType("bank_transfer")
                .user(user.get())
                .amount(topupDetailDto.getAmount())
                .grossAmount(topupDetailDto.getGrossAmount())
                .status("created")
                .build();

        List<String> bankTransferList = new ArrayList<>();
        for(EBankTransfer bankTransfer : EBankTransfer.values()) {
            bankTransferList.add(bankTransfer.value);
        }
        if(!bankTransferList.contains(topupDetailDto.getTransferMethod())){
            return Response.build(Response.notFound("transfer_method"), null, null, HttpStatus.BAD_REQUEST);
        }
        topupDetail.setTransferMethod(topupDetailDto.getTransferMethod());

        topupDetailRepository.save(topupDetail);

        BankTransferBody bankTransferBody = BankTransferBody.builder()
                .paymentType(topupDetail.getPaymentType())
                .orderId(topupDetail.getId().toString())
                .grossAmount(topupDetail.getGrossAmount())
                .bank(topupDetail.getTransferMethod())
                .build();

        log.info("req body {}", bankTransferBody.toString());

        WebClient client = WebClient.create();
        String response = client.post()
                .uri(midtransBaseUrl + "/charge")
                // HttpHeaders.AUTHORIZATION
                .header(HttpHeaders.AUTHORIZATION, midtransAuthHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bankTransferBody.toString())
                .retrieve()
                .bodyToMono(String.class).block();

        BankChargeRes bankChargeRes = new ObjectMapper().readValue(response, BankChargeRes.class);
        if(bankChargeRes.getStatusCode().equals(String.valueOf(HttpStatus.CREATED.value()))){
            topupDetail.setStatus("pending");
            topupDetailRepository.save(topupDetail);
        }else {
            Response.build("internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BankChargeDto bankChargeDto = BankChargeDto.builder()
                .orderId(bankChargeRes.getOrderId())
                .bank(bankChargeRes.getVaNumberList().get(0).getBank())
                .status(topupDetail.getStatus())
                .vaNumber(bankChargeRes.getVaNumberList().get(0).getVaNumber())
                .build();

        return Response.build(Response.create("topup bank transfer"), bankChargeDto, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getNotification(NotificationDto notificationDto) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String notificationString = objectMapper.writeValueAsString(notificationDto);
//        log.info("notification string {}", notificationString);
        String encryptedKey = Encryptor.encryptStringToSHA512(
                notificationDto.getOrderId() +
                        notificationDto.getStatusCode() +
                        notificationDto.getGrossAmount() +
                        midtransServerKey);

        if(!notificationDto.getSignatureKey().equals(encryptedKey)){
            return Response.build("Not authorized", null, null, HttpStatus.BAD_REQUEST);
        }
//        log.info("Sign key : {}", notificationDto.getSignatureKey());
//        log.info("Encrypted key : {}", encryptedKey);

        log.info("Transaction notification received. Order ID: {}. Transaction status: {}. Fraud status: {});",
                notificationDto.getOrderId(),
                notificationDto.getTransactionStatus(),
                notificationDto.getFraudStatus());

//        log.info("Update transaction status and send get status after notification;");
//        WebClient client = WebClient.create();

        Optional<TopupDetail> topupDetail = topupDetailRepository.findById(Long.parseLong(notificationDto.getOrderId()));
        if(notificationDto.getTransactionStatus().equals("capture")){
            if(notificationDto.getFraudStatus().equals("challenge")){
                topupDetail.get().setStatus("challenge");
                topupDetailRepository.save(topupDetail.get());
            }else if(notificationDto.getFraudStatus().equals("accept")){
                topupDetail.get().setStatus("success");
                topupDetailRepository.save(topupDetail.get());
            }
        }else if(notificationDto.getTransactionStatus().equals("settlement")){
            topupDetail.get().setStatus("success");
            topupDetailRepository.save(topupDetail.get());
        }else if(notificationDto.getTransactionStatus().equals("cancel") ||
                notificationDto.getTransactionStatus().equals("deny") ||
                notificationDto.getTransactionStatus().equals("expire")
        ){
            topupDetail.get().setStatus("failure");
            topupDetailRepository.save(topupDetail.get());
        }else if(notificationDto.getTransactionStatus().equals("pending")){
            topupDetail.get().setStatus("pending");
            topupDetailRepository.save(topupDetail.get());
        }

        log.info("Transaction status : {}", topupDetail.get().getStatus());
        return Response.build(Response.update("topup detail"), topupDetail, null, HttpStatus.OK);
    }
}
