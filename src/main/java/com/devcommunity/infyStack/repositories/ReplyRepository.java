package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.documents.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {

}
