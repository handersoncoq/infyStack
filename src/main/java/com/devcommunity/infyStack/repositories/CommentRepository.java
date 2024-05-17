package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.documents.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
}
