package com.alterra.capstone14.domain.thirdparty.req;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Slf4j
public class TransactionDetailBody {
    private String orderId;
    private Long grossAmount;

    public String transactionDetailString() {
        return String.format(
                "{" +
                        "\"order_id\":\"%s\"," +
                        "\"gross_amount\":%d" +
                        "}"
                , orderId, grossAmount);
    }

}
