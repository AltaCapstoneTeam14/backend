package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.QuotaProduct;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.QuotaProductDto;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.repository.TopupProductRepository;
import com.alterra.capstone14.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<Object> addTopupProduct(TopupProductDto topupProductDto) {
        TopupProduct topupProduct = modelMapper.map(topupProductDto, TopupProduct.class);

        topupProductRepository.save(topupProduct);

        TopupProductDto topupProductDto1 = modelMapper.map(topupProduct, TopupProductDto.class);

        return Response.build(Response.add("topup product"), topupProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getTopupProducts() {
        List<TopupProduct> topupProductList = topupProductRepository.findAllSorted();
        List<TopupProductDto> topupProductDtoList = new ArrayList<>();

        topupProductList.forEach(topupProduct -> {
            topupProductDtoList.add(
                    TopupProductDto.builder()
                            .id(topupProduct.getId())
                            .name(topupProduct.getName())
                            .amount(topupProduct.getAmount())
                            .grossAmount(topupProduct.getGrossAmount())
                            .build()
            );
        });

        return Response.build(Response.get("topup product"), topupProductDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateTopupProduct(TopupProductDto topupProductDto, Long id) {
        Optional<TopupProduct> topupProduct = topupProductRepository.findById(id);
        if(topupProduct.isEmpty()){
            return Response.build(Response.notFound("Topup product"), null, null, HttpStatus.BAD_REQUEST);
        }

        topupProduct.get().setName(topupProductDto.getName());
        topupProduct.get().setAmount(topupProductDto.getAmount());
        topupProduct.get().setGrossAmount(topupProductDto.getGrossAmount());
        topupProductRepository.save(topupProduct.get());

        TopupProductDto topupProductDto1 = modelMapper.map(topupProduct, TopupProductDto.class);

        return Response.build(Response.update("topup product"), topupProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteTopupProduct(Long id) {
        Optional<TopupProduct> topupProduct = topupProductRepository.findById(id);
        if(topupProduct.isEmpty()){
            return Response.build(Response.notFound("Topup product"), null, null, HttpStatus.BAD_REQUEST);
        }

        topupProductRepository.delete(topupProduct.get());

        return Response.build(Response.delete("topup product"), null, null, HttpStatus.CREATED);
    }
}


