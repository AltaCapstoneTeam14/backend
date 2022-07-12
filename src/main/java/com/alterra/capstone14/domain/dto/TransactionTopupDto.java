package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionTopupDto {
    @JsonProperty("product_id")
    @NotBlank(message = "product_id is required!")
    private Long productId;

    @NotBlank(message = "transfer_method is required!")
    @JsonProperty("transfer_method")
    private String transferMethod;



}