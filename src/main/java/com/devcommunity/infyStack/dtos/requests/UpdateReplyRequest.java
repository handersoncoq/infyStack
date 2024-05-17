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
public class UpdateReplyRequest {

    @NotBlank(message = "Comment Id is required")
    @NotEmpty(message = "Comment Id cannot be empty")
    @NotNull(message = "Comment Id cannot be null")
    private String replyId;

    @NotBlank(message = "Body Id is required")
    @NotEmpty(message = "Body Id cannot be empty")
    @NotNull(message = "Body Id cannot be null")
    private String body;
}
