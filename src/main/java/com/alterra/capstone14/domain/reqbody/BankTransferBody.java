package com.alterra.capstone14.domain.reqbody;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BankTransferBody extends GopayBody {
    private String bank;

    @Override
    public String toString() {
        return String.format(
                "{" +
                        "\"payment_type\":\"%s\"," +
                        "\"transaction_details\":%s," +
                        "\"bank_transfer\":{" +
                            "\"bank\":\"%s\"" +
                        "}" +
                "}", super.getPaymentType(), super.transactionDetailString(), bank);
    }

//    public String toStringDev() {
//        return String.format(
//                "{" +
//                        "\"payment_type\":\"%s\"," +
//                        "\"transaction_details\":%s," +
//                        "\"bank_transfer\":{" +
//                            "\"bank\":\"%s\"" +
//                        "}" +
//                "}", super.getPaymentType(), super.transactionDetailStringDev(), bank);
//    }
}
