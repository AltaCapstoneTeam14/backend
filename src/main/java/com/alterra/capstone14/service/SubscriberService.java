package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Subscriber;
import com.alterra.capstone14.domain.dto.SubscriberDto;
import com.alterra.capstone14.repository.SubscriberRepository;
import com.alterra.capstone14.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriberService {
    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<Object> addSubscriber(SubscriberDto subscriberDto) {
        Optional<Subscriber> subscriber = subscriberRepository.findByEmail(subscriberDto.getEmail());
        if (subscriber.isEmpty()){
            Subscriber newSubscriber = modelMapper.map(subscriberDto, Subscriber.class);
            subscriberRepository.save(newSubscriber);
            subscriber = Optional.of(newSubscriber);
        }

        SubscriberDto subscriberDto1 = modelMapper.map(subscriber, SubscriberDto.class);

        return Response.build(Response.add("subscriber"), subscriberDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getSubscribers() {
        List<Subscriber> subscriberList = subscriberRepository.findAll();
        List<SubscriberDto> subscriberDtoList = new ArrayList<>();

        subscriberList.forEach(subscriber -> {
            subscriberDtoList.add(SubscriberDto.builder().id(subscriber.getId()).email(subscriber.getEmail()).build());
        });

        return Response.build(Response.get("subscriber"), subscriberDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteSubscriber(Long id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
        if(subscriber.isEmpty()){
            return Response.build(Response.notFound("Subscriber"), null, null, HttpStatus.BAD_REQUEST);
        }

        subscriberRepository.deleteById(id);

        return Response.build(Response.delete("subscriber"), null, null, HttpStatus.CREATED);
    }
}
