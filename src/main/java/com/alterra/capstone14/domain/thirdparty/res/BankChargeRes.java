package com.alterra.capstone14.domain.thirdparty.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankChargeRes extends ChargeRes{

    @JsonProperty("va_numbers")
    private List<VANumber> vaNumberList;
}
