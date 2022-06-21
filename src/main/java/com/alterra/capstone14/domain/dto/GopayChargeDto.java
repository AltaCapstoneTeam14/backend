package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.resBody.GopayAction;
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
    private Long orderId;

    private String status;

    private List<GopayAction> actions;
}
