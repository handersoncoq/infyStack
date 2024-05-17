package com.devcommunity.infyStack.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class NewUserRequest {

    @NotBlank(message = "Email is required.")
    @NotEmpty(message = "Email cannot be empty.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Pseudo name is required.")
    @NotEmpty(message = "Pseudo name cannot be empty.")
    @Pattern(regexp = "^[a-zA-Z\\d]+$", message = "Pseudo name must contain only alphanumeric characters.")
    @Size(max = 20, message = "Pseudonym length must be up to 20 characters.")
    private String pseudoName;

    @NotBlank(message = "First name is required.")
    @NotEmpty(message = "First name cannot be empty.")
    @NotNull(message = "First name cannot be null.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Make sure your first name does not contain wild characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @NotEmpty(message = "Last name cannot be empty.")
    @NotNull(message = "Last name cannot be null.")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Make sure your last name does not contain wild characters.")
    private String lastName;

    @NotBlank(message = "Password is required.")
    @NotEmpty(message = "Password cannot be empty.")
    @NotNull(message = "Password cannot be null.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
             message = "Password must be at least 8 characters long and contain at least one uppercase letter," +
                    "one lowercase letter, and one digit.")
    private String password;

    private List<@Pattern(regexp = "^[a-zA-Z\\d.+#\\-]+$", message = "Invalid input in tech stack.")
            String> techStack = new ArrayList<>();

}