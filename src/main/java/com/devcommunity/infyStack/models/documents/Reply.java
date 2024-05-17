package com.devcommunity.infyStack.models.documents;

import com.devcommunity.infyStack.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "replies")
public class Reply {

    @Id
    private String id;
    @DBRef
    private Comment comment;
    private User author;
    private String body;
    private Integer likes = 0;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
}
