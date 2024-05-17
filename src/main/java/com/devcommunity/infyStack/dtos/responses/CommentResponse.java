package com.devcommunity.infyStack.dtos.responses;

import com.devcommunity.infyStack.models.documents.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private String id;
    private String answerId;
    private String body;
    private String author;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private Integer likes = 0;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.answerId = comment.getAnswer().getId();
        this.author = comment.getAuthor().getPseudoName();
        this.body = comment.getBody();
        this.likes = comment.getLikes();
        this.dateCreated = comment.getDateCreated();
        this.lastEdited = comment.getLastEdited();
    }
}
