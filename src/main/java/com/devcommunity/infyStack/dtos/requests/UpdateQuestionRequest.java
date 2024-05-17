package com.devcommunity.infyStack.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuestionRequest {

    @NotBlank(message = "Question Id is required")
    @NotEmpty(message = "Question Id cannot be empty")
    @NotNull(message = "Question Id cannot be null")
    private String questionId;

    private String title;
    private String body;
    private String code;
    private List<String> tags;
}
