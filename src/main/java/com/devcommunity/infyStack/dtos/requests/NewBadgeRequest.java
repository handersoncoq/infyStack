package com.devcommunity.infyStack.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class NewBadgeRequest {

    @NotBlank(message = "Badge name is required")
    @NotNull(message = "Badge name cannot be blank")
    @NotEmpty(message = "Badge name cannot be empty")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Make sure the badge name does not contain wild characters.")
    private String name;

    @NotBlank(message = "Description is required")
    @NotNull(message = "Description cannot be blank")
    @NotEmpty(message = "Description cannot be empty")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Make sure description does not contain wild characters.")
    private String description;

    @NotBlank(message = "Criteria name cannot be blank")
    @NotEmpty(message = "Criteria name cannot be empty")
    @NotNull(message = "Criteria name cannot be null")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Make sure criteria does not contain wild characters.")
    private String criteria;

    @Min(value = 0, message = "Enter a valid number of question required.")
    private Integer numberOfQuestionRequired;

    @Min(value = 0, message = "Enter a valid number of accepted answer required.")
    private Integer numberOfAcceptedAnsweredRequired;

}