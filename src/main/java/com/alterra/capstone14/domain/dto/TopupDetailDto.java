package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.common.BaseCreatedAt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TopupDetailDto extends BaseCreatedAt {
    private Long id;

    private UserDto user;

    @NotBlank(message = "Amount is required!")
    private Integer amount;

    @NotBlank(message = "Gross amount is required!")
    @JsonProperty("gross_amount")
    private Integer grossAmount;

    @NotBlank(message = "Payment type is required!")
    @JsonProperty("payment_type")
    private String paymentType;

    @NotBlank(message = "Transfer method is required!")
    @JsonProperty("transfer_method")
    private String transferMethod;

    private String status;
}