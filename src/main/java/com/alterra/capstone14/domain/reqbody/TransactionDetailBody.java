package com.alterra.capstone14.domain.reqbody;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransactionDetailBody {
    private String orderId;
    private Integer grossAmount;

    public String transactionDetailString() {
        return String.format(
                "{" +
                    "\"order_id\":\"%s\"," +
                    "\"gross_amount\":%d" +
                "}"
        , orderId, grossAmount);
    }
}
