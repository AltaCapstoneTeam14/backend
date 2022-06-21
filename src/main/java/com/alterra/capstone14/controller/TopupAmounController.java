package com.alterra.capstone14.controller;

import com.alterra.capstone14.domain.dto.TopupAmountDto;
import com.alterra.capstone14.domain.dto.UserNoPwdDto;
import com.alterra.capstone14.service.TopupAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/topup-amount")
public class TopupAmounController {
    @Autowired
    TopupAmountService topupAmountService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getTopupAmountList() {
        return topupAmountService.getAmountList();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> addTopupAmount(@RequestBody TopupAmountDto topupAmountDto) {
        return topupAmountService.addNewAmount(topupAmountDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> deleteTopupAmount(@PathVariable Long id) {
        return topupAmountService.deleteAmount(id);
    }
}
