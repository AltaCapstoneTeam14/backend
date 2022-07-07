package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.QuotaProduct;
import com.alterra.capstone14.domain.dto.QuotaProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
import com.alterra.capstone14.repository.QuotaProductRepository;
import com.alterra.capstone14.repository.QuotaProductRepository;
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
public class QuotaProductService {
    @Autowired
    QuotaProductRepository quotaProductRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProviderRepository providerRepository;

    public ResponseEntity<Object> addQuotaProduct(QuotaProductDto quotaProductDto) {
        Optional<Provider> provider = providerRepository.findById(quotaProductDto.getProviderId());
        if(provider.isEmpty()){
            return Response.build(Response.notFound("Provider"), null, null, HttpStatus.BAD_REQUEST);
        }

        QuotaProduct quotaProduct = modelMapper.map(quotaProductDto, QuotaProduct.class);
        quotaProduct.setProvider(provider.get());

        quotaProductRepository.save(quotaProduct);

        QuotaProductDto quotaProductDto1 = QuotaProductDto.builder()
                .id(quotaProduct.getId())
                .name(quotaProduct.getName())
                .description(quotaProduct.getDescription())
                .grossAmount(quotaProduct.getGrossAmount())
                .providerId(quotaProduct.getProvider().getId())
                .providerName(quotaProduct.getProvider().getName())
                .stock(quotaProduct.getStock())
                .build();

        return Response.build(Response.add("quota product"), quotaProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getQuotaProducts() {
        List<QuotaProduct> quotaProductList = quotaProductRepository.findAllSorted();
        List<QuotaProductDto> quotaProductDtoList = new ArrayList<>();

        quotaProductList.forEach(quotaProduct -> {
            quotaProductDtoList.add(QuotaProductDto.builder()
                    .id(quotaProduct.getId())
                    .name(quotaProduct.getName())
                    .description(quotaProduct.getDescription())
                    .grossAmount(quotaProduct.getGrossAmount())
                    .providerId(quotaProduct.getProvider().getId())
                    .providerName(quotaProduct.getProvider().getName())
                    .stock(quotaProduct.getStock())
                    .build());
        });

        return Response.build(Response.get("quota product"), quotaProductDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateQuotaProduct(QuotaProductDto quotaProductDto, Long id) {
        Optional<Provider> provider = providerRepository.findById(quotaProductDto.getProviderId());
        if(provider.isEmpty()){
            return Response.build(Response.notFound("Provider"), null, null, HttpStatus.BAD_REQUEST);
        }

        Optional<QuotaProduct> quotaProduct = quotaProductRepository.findById(id);
        if(quotaProduct.isEmpty()){
            return Response.build(Response.notFound("Quota product"), null, null, HttpStatus.BAD_REQUEST);
        }

        quotaProduct.get().setName(quotaProductDto.getName());
        quotaProduct.get().setDescription(quotaProductDto.getDescription());
        quotaProduct.get().setGrossAmount(quotaProductDto.getGrossAmount());
        quotaProduct.get().setStock(quotaProductDto.getStock());
        quotaProduct.get().setProvider(provider.get());

        quotaProductRepository.save(quotaProduct.get());

        QuotaProductDto quotaProductDto1 = QuotaProductDto.builder()
                .id(quotaProduct.get().getId())
                .name(quotaProduct.get().getName())
                .description(quotaProduct.get().getDescription())
                .grossAmount(quotaProduct.get().getGrossAmount())
                .providerId(quotaProduct.get().getProvider().getId())
                .providerName(quotaProduct.get().getProvider().getName())
                .stock(quotaProduct.get().getStock())
                .build();

        return Response.build(Response.update("quota product"), quotaProductDto1, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteQuotaProduct(Long id) {
        Optional<QuotaProduct> quotaProduct = quotaProductRepository.findById(id);
        if(quotaProduct.isEmpty()){
            return Response.build(Response.notFound("Quota product"), null, null, HttpStatus.BAD_REQUEST);
        }

        quotaProductRepository.deleteById(id);

        return Response.build(Response.delete("quota product"), null, null, HttpStatus.CREATED);
    }
}
