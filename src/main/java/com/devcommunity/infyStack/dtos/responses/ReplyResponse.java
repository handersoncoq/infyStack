package com.devcommunity.infyStack.dtos.responses;


import com.devcommunity.infyStack.models.documents.Reply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponse {

    private String id;
    private String commentId;
    private String body;
    private String author;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private Integer likes = 0;

    public ReplyResponse(Reply reply) {
        this.id = reply.getId();
        this.commentId = reply.getComment().getId();
        this.author = reply.getAuthor().getPseudoName();
        this.body = reply.getBody();
        this.likes = reply.getLikes();
        this.dateCreated = reply.getDateCreated();
        this.lastEdited = reply.getLastEdited();
    }
}
