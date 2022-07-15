package com.alterra.capstone14.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryDto <T>{
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("transaction_detail_id")
    private Long transactionDetailId;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("product_history_id")
    private Long productHistoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gross_amount")
    private Long grossAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("transfer_method")
    private String transferMethod;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("date_string")
    private String dateString;

    @JsonProperty("product")
    private T product;
}
