package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.models.entities.Vote;
import com.devcommunity.infyStack.repositories.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
@AllArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepo;

    public void save(Vote vote){
        voteRepo.save(vote);
    }

    public Vote getVoteByUserIdAndAnswerId(Long userId, String answerId){
        return voteRepo.findByUserIdEqualsAndAnswerIdEquals(userId, answerId);
    }

}
