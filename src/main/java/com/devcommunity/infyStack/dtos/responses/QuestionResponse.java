package com.devcommunity.infyStack.dtos.responses;
import com.devcommunity.infyStack.models.documents.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {

    private String id;
    private String title;
    private String body;
    private String code;
    private String author;
    private List<String> tags = new ArrayList<>();
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private Integer views;
    private Boolean hasAcceptedAnswer = false;
    private Integer answers;

    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.body = question.getBody();
        this.code= question.getCode();
        this.dateCreated = question.getDateCreated();
        this.lastEdited = question.getLastEdited();
        question.getTags().forEach( (tag) -> this.tags.add(tag.getName()));
        this.author = question.getAuthor().getPseudoName();
        this.views = question.getViews();
        this.hasAcceptedAnswer = question.getHasAcceptedAnswer();
        this.answers = question.getAnswers().size();

    }
}
