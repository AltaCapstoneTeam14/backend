package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.common.BaseIsDeleted;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends UserNoPwdDto {

    @NotBlank(message = "Password is required!")
    @Size(min = 8, message = "The length of phone must be at least 8 characters.")
    private String password;

}
