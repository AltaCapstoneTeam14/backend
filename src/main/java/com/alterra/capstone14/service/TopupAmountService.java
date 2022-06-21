package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.TopupAmount;
import com.alterra.capstone14.domain.dto.TopupAmountDto;
import com.alterra.capstone14.repository.TopupAmountRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TopupAmountService {
    @Autowired
    TopupAmountRepository topupAmountRepository;

    public ResponseEntity<Object> addNewAmount(TopupAmountDto topupAmountDto) {

        TopupAmount topupAmount = TopupAmount.builder()
                .amount(topupAmountDto.getAmount())
                .build();

        topupAmountRepository.save(topupAmount);

        TopupAmountDto topupAmountDto1 = TopupAmountDto.builder()
                .id(topupAmount.getId())
                .amount(topupAmount.getAmount())
                .build();

        return Response.build(Response.add("topup amount"), topupAmountDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getAmountList() {
        List<TopupAmount> topupAmounts = topupAmountRepository.findAll();
        List<TopupAmountDto> topupAmountDtos = new ArrayList<>();

        topupAmounts.forEach(amount -> {
            topupAmountDtos.add(TopupAmountDto.builder().id(amount.getId()).amount(amount.getAmount()).build());
        });

        return Response.build(Response.get("topup amount"), topupAmountDtos, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteAmount(Long id) {
        Optional<TopupAmount> topupAmount = topupAmountRepository.findById(id);
        if(topupAmount.isEmpty()){
            return Response.build(Response.notFound("topup amount"), null, null, HttpStatus.BAD_REQUEST);
        }

        topupAmountRepository.delete(topupAmount.get());

        return Response.build(Response.delete("topup amount"), null, null, HttpStatus.CREATED);
    }
}
