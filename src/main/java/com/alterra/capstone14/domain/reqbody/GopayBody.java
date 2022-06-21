package com.alterra.capstone14.domain.reqbody;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GopayBody extends TransactionDetailBody {
    private String paymentType;

    @Override
    public String toString() {
        return String.format(
                "{" +
                        "\"payment_type\":\"%s\"," +
                        "\"transaction_details\":%s" +
                "}", paymentType, super.transactionDetailString());
    }
}
