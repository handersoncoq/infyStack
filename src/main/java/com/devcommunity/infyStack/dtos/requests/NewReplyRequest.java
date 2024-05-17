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
public class NewReplyRequest {

    @NotBlank(message = "Comment Id is required")
    @NotEmpty(message = "Comment Id cannot be empty")
    @NotNull(message = "Comment Id cannot be null")
    private String CommentId;

    @NotBlank(message = "Body is required")
    @NotEmpty(message = "Body cannot be empty")
    @NotNull(message = "Body cannot be null")
    private String body;
}
