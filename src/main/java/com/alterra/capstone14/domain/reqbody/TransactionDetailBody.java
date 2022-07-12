package com.alterra.capstone14.domain.reqbody;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.beans.factory.annotation.Value;

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

//    public String transactionDetailStringDev() {
//        return String.format(
//                "{" +
//                        "\"order_id\":\"tdev-%s\"," +
//                        "\"gross_amount\":%d" +
//                        "}"
//                , orderId, grossAmount);
//    }
}
