package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.documents.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {
}
