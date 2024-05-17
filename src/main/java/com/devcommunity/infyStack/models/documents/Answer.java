package com.devcommunity.infyStack.models.documents;

import com.devcommunity.infyStack.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "answers")
public class Answer {

    @Id
    private String id;
    private String questionId;
    private User author;
    private String body;
    private String code;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private Integer upVote = 0;
    private Integer downVote = 0;
    private Integer score = 0;
    private Boolean isAccepted = false;
    private List<String> commentIds = new ArrayList<>();
}
