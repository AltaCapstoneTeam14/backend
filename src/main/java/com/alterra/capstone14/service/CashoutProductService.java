package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.CashoutProduct;
import com.alterra.capstone14.domain.dto.CashoutProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
import com.alterra.capstone14.repository.CashoutProductRepository;
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
public class CashoutProductService {
    @Autowired
    CashoutProductRepository cashoutProductRepository;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<Object> addCashoutProduct(CashoutProductDto cashoutProductDto) {

        CashoutProduct cashoutProduct = modelMapper.map(cashoutProductDto, CashoutProduct.class);

        cashoutProductRepository.save(cashoutProduct);

        CashoutProductDto cashoutProductDto1 = modelMapper.map(cashoutProduct, CashoutProductDto.class);

        return Response.build(Response.add("cashout product"), cashoutProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getCashoutProducts() {
        List<CashoutProduct> cashoutProductList = cashoutProductRepository.findAllSorted();
        List<CashoutProductDto> cashoutProductDtoList = new ArrayList<>();

        cashoutProductList.forEach(cashoutProduct -> {
            cashoutProductDtoList.add(CashoutProductDto.builder()
                    .id(cashoutProduct.getId())
                    .name(cashoutProduct.getName())
                    .coinAmount(cashoutProduct.getCoinAmount())
                    .balanceAmount(cashoutProduct.getBalanceAmount())
                    .build());
        });

        return Response.build(Response.get("cashout product"), cashoutProductDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateCashoutProduct(CashoutProductDto cashoutProductDto, Long id) {
        Optional<CashoutProduct> cashoutProduct = cashoutProductRepository.findById(id);
        if(cashoutProduct.isEmpty()){
            return Response.build(Response.notFound("Cashout product"), null, null, HttpStatus.BAD_REQUEST);
        }

        cashoutProduct.get().setName(cashoutProductDto.getName());
        cashoutProduct.get().setCoinAmount(cashoutProductDto.getCoinAmount());
        cashoutProduct.get().setBalanceAmount(cashoutProductDto.getBalanceAmount());

        cashoutProductRepository.save(cashoutProduct.get());

        CashoutProductDto cashoutProductDto1 = CashoutProductDto.builder()
                .id(cashoutProduct.get().getId())
                .name(cashoutProduct.get().getName())
                .balanceAmount(cashoutProduct.get().getBalanceAmount())
                .coinAmount(cashoutProduct.get().getCoinAmount())
                .build();

        return Response.build(Response.update("cashout product"), cashoutProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteCashoutProduct(Long id) {
        Optional<CashoutProduct> cashoutProduct = cashoutProductRepository.findById(id);
        if(cashoutProduct.isEmpty()){
            return Response.build(Response.notFound("Cashout product"), null, null, HttpStatus.BAD_REQUEST);
        }

        cashoutProductRepository.deleteById(id);

        return Response.build(Response.delete("cashout product"), null, null, HttpStatus.CREATED);
    }
}
