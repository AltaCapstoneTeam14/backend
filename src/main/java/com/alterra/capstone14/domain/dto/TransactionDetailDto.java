package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.common.BaseCreatedAt;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransactionDetailDto extends BaseCreatedAt {
    private Long id;

    @JsonProperty("order_id")
    private String orderId;

    private UserDto user;

    @NotBlank(message = "product_type amount is required!")
    @JsonProperty("product_type")
    private String productType;

    @NotNull(message = "gross_amount is required")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "gross_amount is required")
    @Min(value = 1, message = "gross_amount is at least 1")
    @JsonProperty("gross_amount")
    private Long grossAmount;

    @JsonProperty("payment_type")
    private String paymentType;

    @NotBlank(message = "transfer_method is required!")
    @JsonProperty("transfer_method")
    private String transferMethod;

    private String status;

    @JsonProperty("json_notification")
    private String jsonNotification;
}