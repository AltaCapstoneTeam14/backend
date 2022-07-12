package com.alterra.capstone14.domain.dto;

import com.alterra.capstone14.domain.common.BaseIsDeleted;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNoPwdDto extends BaseIsDeleted {
    private Long id;

    @NotBlank(message = "Name is required!")
    private String name;

    @NotBlank(message = "Email is required!")
    @Email(message = "The email address is invalid.")
    private String email;

    @NotBlank(message = "Phone is required!")
    @Pattern(regexp = "^[0-9]+$", message="Phone value must be number")
    @Size(min = 10, max = 18, message = "The length of phone must be between 10 and 18 characters.")
    private String phone;

    private Set<String> roles;
}