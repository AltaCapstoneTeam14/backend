package com.alterra.capstone14.service;

import com.alterra.capstone14.domain.dao.Provider;
import com.alterra.capstone14.domain.dao.TopupProduct;
import com.alterra.capstone14.domain.dto.ProviderDto;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.repository.ProviderRepository;
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
public class ProviderService {
    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    ModelMapper modelMapper;

    public ResponseEntity<Object> addProvider(ProviderDto providerDto) {
        Provider provider = modelMapper.map(providerDto, Provider.class);

        providerRepository.save(provider);

        ProviderDto providerDto1 = modelMapper.map(provider, ProviderDto.class);

        return Response.build(Response.add("provider"), provider, null, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> getProviders() {
        List<Provider> providerList = providerRepository.findAll();
        List<ProviderDto> providerDtoList = new ArrayList<>();

        providerList.forEach(provider -> {
            providerDtoList.add(ProviderDto.builder().id(provider.getId()).name(provider.getName()).build());
        });

        return Response.build(Response.get("provider"), providerDtoList, null, HttpStatus.OK);
    }

    public ResponseEntity<Object> deleteProvider(Long id) {
        Optional<Provider> provider = providerRepository.findById(id);
        if(provider.isEmpty()){
            return Response.build(Response.notFound("provider"), null, null, HttpStatus.BAD_REQUEST);
        }

        providerRepository.deleteById(id);

        return Response.build(Response.delete("provider"), null, null, HttpStatus.CREATED);
    }

}
