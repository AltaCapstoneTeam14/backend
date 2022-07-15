package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.thirdparty.res.GopayAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GopayChargeDto {
    @JsonProperty("order_id")
    private String orderId;

    private String status;

    private List<GopayAction> actions;
}
