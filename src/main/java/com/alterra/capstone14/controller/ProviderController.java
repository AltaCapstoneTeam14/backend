package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.ProviderDto;
import com.alterra.capstone14.domain.dto.TopupProductDto;
import com.alterra.capstone14.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/products/provider")
public class ProviderController {
    @Autowired
    ProviderService providerService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getProviderList() {
        return providerService.getProviders();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addProvider(@Valid @RequestBody ProviderDto providerDto) {
        return providerService.addProvider(providerDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteProvider(@PathVariable Long id) {
        return providerService.deleteProvider(id);
    }
}
