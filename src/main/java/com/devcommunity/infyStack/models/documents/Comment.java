package com.devcommunity.infyStack.models.documents;

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

@Document(collection = "comments")
public class Comment {

    @Id
    private String id;
    @DBRef
    private Answer answer;
    private User author;
    private String body;
    private Integer likes = 0;
    private LocalDateTime dateCreated;
    private LocalDateTime lastEdited;
    private List<String> replyIds = new ArrayList<>();
}
