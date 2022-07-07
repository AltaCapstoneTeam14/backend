package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.PulsaProduct;
import com.alterra.capstone14.domain.dto.PulsaProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
import com.alterra.capstone14.repository.PulsaProductRepository;
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
public class PulsaProductService {
    @Autowired
    PulsaProductRepository pulsaProductRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProviderRepository providerRepository;

    public ResponseEntity<Object> addPulsaProduct(PulsaProductDto pulsaProductDto) {
        Optional<Provider> provider = providerRepository.findById(pulsaProductDto.getProviderId());
        if(provider.isEmpty()){
            return Response.build(Response.notFound("Provider"), null, null, HttpStatus.BAD_REQUEST);
        }

        PulsaProduct pulsaProduct = modelMapper.map(pulsaProductDto, PulsaProduct.class);
        pulsaProduct.setProvider(provider.get());

        pulsaProductRepository.save(pulsaProduct);

        PulsaProductDto pulsaProductDto1 = PulsaProductDto.builder()
                .id(pulsaProduct.getId())
                .name(pulsaProduct.getName())
                .denom(pulsaProduct.getDenom())
                .grossAmount(pulsaProduct.getGrossAmount())
                .providerId(pulsaProduct.getProvider().getId())
                .providerName(pulsaProduct.getProvider().getName())
                .stock(pulsaProduct.getStock())
                .build();

        return Response.build(Response.add("pulsa product"), pulsaProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getPulsaProducts() {
        List<PulsaProduct> pulsaProductList = pulsaProductRepository.findAllSorted();
        List<PulsaProductDto> pulsaProductDtoList = new ArrayList<>();

        pulsaProductList.forEach(pulsaProduct -> {
            pulsaProductDtoList.add(PulsaProductDto.builder()
                    .id(pulsaProduct.getId())
                    .name(pulsaProduct.getName())
                    .denom(pulsaProduct.getDenom())
                    .grossAmount(pulsaProduct.getGrossAmount())
                    .providerId(pulsaProduct.getProvider().getId())
                    .providerName(pulsaProduct.getProvider().getName())
                    .stock(pulsaProduct.getStock())
                    .build());
        });

        return Response.build(Response.get("pulsa product"), pulsaProductDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updatePulsaProduct(PulsaProductDto pulsaProductDto, Long id) {
        Optional<Provider> provider = providerRepository.findById(pulsaProductDto.getProviderId());
        if(provider.isEmpty()){
            return Response.build(Response.notFound("Provider"), null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<PulsaProduct> pulsaProduct = pulsaProductRepository.findById(id);
        if(pulsaProduct.isEmpty()){
            return Response.build(Response.notFound("Pulsa product"), null, null, HttpStatus.BAD_REQUEST);
        }

        pulsaProduct.get().setName(pulsaProductDto.getName());
        pulsaProduct.get().setDenom(pulsaProductDto.getDenom());
        pulsaProduct.get().setGrossAmount(pulsaProductDto.getGrossAmount());
        pulsaProduct.get().setStock(pulsaProductDto.getStock());
        pulsaProduct.get().setProvider(provider.get());

        pulsaProductRepository.save(pulsaProduct.get());

        PulsaProductDto pulsaProductDto1 = PulsaProductDto.builder()
                .id(pulsaProduct.get().getId())
                .name(pulsaProduct.get().getName())
                .denom(pulsaProduct.get().getDenom())
                .grossAmount(pulsaProduct.get().getGrossAmount())
                .providerId(pulsaProduct.get().getProvider().getId())
                .providerName(pulsaProduct.get().getProvider().getName())
                .stock(pulsaProduct.get().getStock())
                .build();

        return Response.build(Response.update("pulsa product"), pulsaProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deletePulsaProduct(Long id) {
        Optional<PulsaProduct> pulsaProduct = pulsaProductRepository.findById(id);
        if(pulsaProduct.isEmpty()){
            return Response.build(Response.notFound("Pulsa product"), null, null, HttpStatus.BAD_REQUEST);
        }

        pulsaProductRepository.deleteById(id);

        return Response.build(Response.delete("pulsa product"), null, null, HttpStatus.CREATED);
    }
}
