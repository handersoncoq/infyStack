package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.documents.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
}
