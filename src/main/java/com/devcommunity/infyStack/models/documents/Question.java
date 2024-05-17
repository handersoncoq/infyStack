package com.devcommunity.infyStack.models.documents;

import com.devcommunity.infyStack.dtos.requests.NewQuestionRequest;
import com.devcommunity.infyStack.models.entities.Tag;
import com.devcommunity.infyStack.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "questions")
public class Question {

    @Id
    private String id;
    private User author;
    private String title;
    private String body;
    private String code;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private List<Tag> tags;
    private Integer views;
    private Boolean hasAcceptedAnswer = false;
    @DBRef
    private List<Answer> answers;
    public Question(NewQuestionRequest newQuestionRequest){
        this.title = newQuestionRequest.getTitle();
        this.body = newQuestionRequest.getBody();
        this.code = newQuestionRequest.getCode();
        this.tags  = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.views = 0;
    }

}
