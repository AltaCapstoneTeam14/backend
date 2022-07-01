package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.common.BaseCreatedAt;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDetailWithCoinDto extends TransactionDetailDto {

    @JsonProperty("coin_earned")
    private Long coinEarned;

}