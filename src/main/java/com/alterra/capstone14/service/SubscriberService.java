package com.alterra.capstone14.service;

import com.alterra.capstone14.constant.EResponseStatus;
import com.alterra.capstone14.constant.ESIBResponseCode;
import com.alterra.capstone14.domain.dao.Subscriber;
import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.domain.thirdparty.res.AllSubscriberRes;
import com.alterra.capstone14.domain.thirdparty.res.NegativeResponseSIB;
import com.alterra.capstone14.repository.SubscriberRepository;
import com.alterra.capstone14.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class SubscriberService {
    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    WebClient webClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${sendinblue.baseurl}")
    private String sibBaseUrl;

    @Value("${sendinblue.api.key}")
    private String sibApiKey;

    public ResponseEntity<Object> addSubscriber(SubscriberDto subscriberDto) {
        try {
            Optional<Subscriber> subscriber = subscriberRepository.findByEmail(subscriberDto.getEmail());
            if (subscriber.isEmpty()){
                Subscriber newSubscriber = modelMapper.map(subscriberDto, Subscriber.class);
                subscriberRepository.save(newSubscriber);
                subscriber = Optional.of(newSubscriber);
            }

            String response = webClient.post()
                    .uri(sibBaseUrl + "/v3/contacts")
                    .header("api-key", sibApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(subscriberDto.toString())
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return Mono.just(EResponseStatus.SUCCESS.value);
                        }
                        return clientResponse.bodyToMono(String.class);
                    }).block();

            if(response == null ){
                throw new NullPointerException("Sendinblue response is null");
            }else if(!response.equals(EResponseStatus.SUCCESS.value)){
                NegativeResponseSIB negativeResponseSIB = objectMapper.readValue(response, NegativeResponseSIB.class);
                if (negativeResponseSIB.getCode().equals(ESIBResponseCode.DUPLICATE_PARAMETER.value)){
                    return Response.build("Already subscribed", null, null, HttpStatus.BAD_REQUEST);
                } else {
                    return Response.build("Bad request", null, null, HttpStatus.BAD_REQUEST);
                }
            }

            SubscriberDto subscriberDto1 = modelMapper.map(subscriber, SubscriberDto.class);

            return Response.build(Response.add("subscriber"), subscriberDto1, null, HttpStatus.CREATED);
        } catch (Exception e){
            log.info("Error : {}", e.getMessage());
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getSubscribers() {
        try {
            String response = webClient.get()
                    .uri(sibBaseUrl + "/v3/contacts")
                    .header("api-key", sibApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(clientResponse -> {
                        if (!clientResponse.statusCode().is2xxSuccessful()) {
                            return Mono.just(EResponseStatus.BAD_REQUEST.value);
                        }
                        return clientResponse.bodyToMono(String.class);
                    }).block();

            if(response == null ){
                throw new NullPointerException("Sendinblue response is null");
            }else if(response.equals(EResponseStatus.BAD_REQUEST.value)){
                return Response.build("Bad request", null, null, HttpStatus.BAD_REQUEST);
            }

            AllSubscriberRes allSubscriberRes = objectMapper.readValue(response, AllSubscriberRes.class);

            return Response.build(Response.get("subscriber"), allSubscriberRes.getContacts(), null, HttpStatus.OK);
        } catch (Exception e){
            log.info("Error : {}", e.getMessage());
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteSubscriber(String email) {
        try {
            Optional<Subscriber> subscriber = subscriberRepository.findByEmail(email);
            subscriber.ifPresent(value -> subscriberRepository.deleteById(value.getId()));

            String response = webClient.delete()
                    .uri(sibBaseUrl + "/v3/contacts/" + email)
                    .header("api-key", sibApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {
                            return Mono.just(EResponseStatus.SUCCESS.value);
                        } else if (clientResponse.rawStatusCode() == 404){
                            return Mono.just(EResponseStatus.NOT_FOUND.value);
                        } else {
                            return Mono.just(EResponseStatus.BAD_REQUEST.value);
                        }
                    }).block();

            if(response == null ){
                throw new NullPointerException("Sendinblue response is null");
            }else if(!response.equals(EResponseStatus.SUCCESS.value)){
                if (response.equals(EResponseStatus.NOT_FOUND.value)){
                    return Response.build(Response.notFound("Subscriber"), null, null, HttpStatus.BAD_REQUEST);
                } else {
                    return Response.build("Bad request", null, null, HttpStatus.BAD_REQUEST);
                }
            }

            return Response.build(Response.delete("subscriber"), null, null, HttpStatus.CREATED);
        } catch (Exception e){
            log.info("Error : {}", e.getMessage());
            return Response.build("Internal server error", null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
