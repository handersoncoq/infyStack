package com.devcommunity.infyStack.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "Email is required")
    @NotEmpty(message = "Email cannot be empty")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotBlank(message = "Old password is required")
    @NotEmpty(message = "Old Password cannot be empty")
    @NotNull(message = "Old Password cannot be null")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @NotEmpty(message = "New password cannot be empty")
    @NotNull(message = "New password cannot be null")
    private String newPassword;
}
