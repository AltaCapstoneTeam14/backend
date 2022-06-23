package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.repository.TopupProductRepository;
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
public class TopupProductService {
    @Autowired
    TopupProductRepository topupProductRepository;

    public ResponseEntity<Object> addTopupProduct(TopupProductDto topupProductDto) {

        TopupProduct topupProduct = TopupProduct.builder()
                .amount(topupProductDto.getAmount())
                .grossAmount(topupProductDto.getGrossAmount())
                .build();

        topupProductRepository.save(topupProduct);

        TopupProductDto topupProductDto1 = TopupProductDto.builder()
                .id(topupProduct.getId())
                .amount(topupProduct.getAmount())
                .grossAmount(topupProduct.getGrossAmount())
                .build();

        return Response.build(Response.add("topup product"), topupProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getTopupProducts() {
        List<TopupProduct> topupProductList = topupProductRepository.findAll();
        List<TopupProductDto> topupProductDtoList = new ArrayList<>();

        topupProductList.forEach(amount -> {
            topupProductDtoList.add(
                    TopupProductDto.builder()
                            .id(amount.getId())
                            .amount(amount.getAmount())
                            .grossAmount(amount.getGrossAmount())
                            .build()
            );
        });

        return Response.build(Response.get("topup product"), topupProductDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteTopupProduct(Long id) {
        Optional<TopupProduct> topupProduct = topupProductRepository.findById(id);
        if(topupProduct.isEmpty()){
            return Response.build(Response.notFound("topup product"), null, null, HttpStatus.BAD_REQUEST);
        }

        topupProductRepository.delete(topupProduct.get());

        return Response.build(Response.delete("topup product"), null, null, HttpStatus.CREATED);
    }
}
