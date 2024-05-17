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
public class NewQuestionRequest {

    @NotBlank(message = "A title is required")
    @NotEmpty(message = "Title cannot be empty")
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "Question body is required")
    @NotEmpty(message = "Question body cannot be empty")
    @NotNull(message = "Question body cannot be null")
    private String body;
    private String code;

    @NotEmpty(message = "Tags cannot be empty")
    @NotNull(message = "Tags cannot be null")
    private List<String> tags;
}
