package com.devcommunity.infyStack.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewVoteRequest {

    @NotBlank(message = "Question Id is required")
    @NotEmpty(message = "Question Id cannot be empty")
    @NotNull(message = "Question Id cannot be null")
    private String answerId;

    @NotNull(message = "Value cannot be null")
    @Range(min = -1, max = 1, message = "Please enter a value between -1 and 1")
    private Integer value;
}
