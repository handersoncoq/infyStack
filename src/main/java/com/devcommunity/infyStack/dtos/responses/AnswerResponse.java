package com.devcommunity.infyStack.dtos.responses;


import com.devcommunity.infyStack.models.documents.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {

    private String id;
    private String questionId;
    private String body;
    private String code;
    private String author;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private Integer score = 0;
    private Boolean isAccepted = false;
    private Integer comments;

    public AnswerResponse(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestionId();
        this.author = answer.getAuthor().getPseudoName();
        this.body = answer.getBody();
        this.code = answer.getCode();
        this.dateCreated = answer.getDateCreated();
        this.lastEdited = answer.getLastEdited();
        this.score = answer.getScore();
        this.isAccepted = answer.getIsAccepted();
        this.comments = answer.getCommentIds().size();
    }
}
