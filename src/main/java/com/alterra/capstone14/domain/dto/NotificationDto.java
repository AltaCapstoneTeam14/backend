package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {
    @JsonProperty("status_code")
    @NotBlank(message = "Status Code is required!")
    private String statusCode;

    @JsonProperty("order_id")
    @NotBlank(message = "Order Id is required!")
    private String orderId;

    @JsonProperty("gross_amount")
    @NotBlank(message = "Gross Amount Id is required!")
    private String grossAmount;

    @JsonProperty("signature_key")
    @NotBlank(message = "Signature Key is required!")
    private String signatureKey;

    @JsonProperty("transaction_status")
    @NotBlank(message = "Transaction Status is required!")
    private String transactionStatus;

    @JsonProperty("fraud_status")
    @NotBlank(message = "Fraud Status is required!")
    private String fraudStatus;
}
