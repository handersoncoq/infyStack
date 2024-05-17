package com.devcommunity.infyStack.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnswerRequest {

    @NotBlank(message = "Answer Id is required")
    @NotEmpty(message = "Answer Id cannot be empty")
    @NotNull(message = "Answer Id cannot be null")
    private String answerId;

    @NotBlank(message = "Body Id is required")
    @NotEmpty(message = "Body Id cannot be empty")
    @NotNull(message = "Body Id cannot be null")
    private String body;
    private String code;
}
