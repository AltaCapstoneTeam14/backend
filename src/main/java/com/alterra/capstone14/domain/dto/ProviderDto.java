package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.dao.PulsaProduct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderDto {
    private Long id;

    @NotBlank(message = "name is required!")
    private String name;

    private List<PulsaProduct> pulsaProductList;
}
