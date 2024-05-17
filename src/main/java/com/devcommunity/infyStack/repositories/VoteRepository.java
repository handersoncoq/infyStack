package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Query("select v from Vote v where v.user.id = ?1 and v.answerId = ?2")
    Vote findByUserIdEqualsAndAnswerIdEquals(Long id, String answerId);


}