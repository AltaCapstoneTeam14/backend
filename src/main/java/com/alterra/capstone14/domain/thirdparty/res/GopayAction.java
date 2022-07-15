package com.alterra.capstone14.domain.thirdparty.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GopayAction {
    private String name;
    private String method;
    private String url;

    @Override
    public String toString() {
        return "[" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                "]";
    }
}
