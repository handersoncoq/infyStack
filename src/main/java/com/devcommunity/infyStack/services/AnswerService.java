package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.dtos.requests.NewAnswerRequest;
import com.devcommunity.infyStack.dtos.requests.NewVoteRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateAnswerRequest;
import com.devcommunity.infyStack.dtos.responses.AnswerResponse;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import com.devcommunity.infyStack.exceptions.InvalidInputException;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.exceptions.ResourcePersistenceException;
import com.devcommunity.infyStack.models.documents.Answer;
import com.devcommunity.infyStack.models.documents.Question;
import com.devcommunity.infyStack.models.entities.Badge;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.models.entities.Vote;
import com.devcommunity.infyStack.repositories.AnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AnswerService {

    private AnswerRepository answerRepo;
    private UserService userService;
    private QuestionService questionService;
    private final BadgeService badgeService;
    private final VoteService voteService;
    public AnswerResponse answerQuestion(String token, NewAnswerRequest newAnswerRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // check if user has already submitted a response for the question
        Question foundQuestion = questionService.getQuestionById(newAnswerRequest.getQuestionId());
        List<Answer> answersToFoundQuestion = foundQuestion.getAnswers();
        List<User> answerAuthorIds = answersToFoundQuestion.stream()
                                                .map(Answer::getAuthor)
                                                .collect(Collectors.toList());
        if(answerAuthorIds.contains(sessionUser)){
            throw new ResourcePersistenceException("You have already submitted an answer for this question. " +
                    "Please update it instead.");
        }

        // instantiate new answer
        Answer newAnswer = new Answer();
        newAnswer.setQuestionId(foundQuestion.getId());
        newAnswer.setAuthor(sessionUser);
        newAnswer.setBody(newAnswerRequest.getBody());
        newAnswer.setCode(newAnswerRequest.getCode());
        newAnswer.setDateCreated(LocalDateTime.now());
        answerRepo.save(newAnswer);

        // update found question's list of answers
        foundQuestion.getAnswers().add(newAnswer);
        questionService.save(foundQuestion);

        // update user's answer list
        sessionUser.getAnswerIds().add(newAnswer.getId());
        userService.save(sessionUser);

        // assign badge if user's qualified
        assignBadge(sessionUser);

        return new AnswerResponse(newAnswer);

    }

    public void updateAnswer(String token, UpdateAnswerRequest updateRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // make sure user is editing their own answer
        if(!sessionUser.getAnswerIds().contains(updateRequest.getAnswerId()))
            throw new AccessDeniedException("You cannot modify this answer");

        // find answer
        Answer foundAnswer = getAnswerById(updateRequest.getAnswerId());

        // update answer body if exits
        if(updateRequest.getBody() != null && !updateRequest.getBody().strip().equals("")){
            foundAnswer.setBody(updateRequest.getBody());
        }

        // update answer code if exits
        if(updateRequest.getCode() != null && !updateRequest.getCode().strip().equals("")){
            foundAnswer.setCode(updateRequest.getCode());
        }

        // update and save found answer
        foundAnswer.setLastEdited(LocalDateTime.now());
        answerRepo.save(foundAnswer);
    }

    public void assignBadge(User user){
        List<String> answerIds = user.getAnswerIds();
        AtomicInteger numberOfAcceptedAnswer = new AtomicInteger();
        answerIds.forEach( answerId ->{
            Answer answer = answerRepo.findById(answerId).orElseThrow();
            if(answer.getIsAccepted()) numberOfAcceptedAnswer.getAndIncrement();
        });
        Badge matchedBadge = badgeService.getByNumberOfAcceptedAnsweredRequired(numberOfAcceptedAnswer.get());
        if(numberOfAcceptedAnswer.get() == 0 || matchedBadge == null) return;
        user.setBadge(matchedBadge);
        userService.save(user);
    }

    public Answer getAnswerById(String id){
        return answerRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Answer does not exist")
        );
    }

    public void save(Answer answer){
        answerRepo.save(answer);
    }

    public void acceptAnswer(String token, String answerId){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find answer
        Answer foundAnswer = getAnswerById(answerId);

        // is the session user the author of the question?
        Question foundQuestion = questionService.getQuestionById(foundAnswer.getQuestionId());
        if(!Objects.equals(sessionUser, foundQuestion.getAuthor()))
            throw new AccessDeniedException("You do not meet the conditions to approve this answer");

        // check if question already has an accepted answer
        if(foundQuestion.getHasAcceptedAnswer())
            throw new InvalidInputException("This question already has an accepted answer.");

        // update and save found answer
        foundAnswer.setIsAccepted(true);
        answerRepo.save(foundAnswer);

        // update question's accepted answer
        foundQuestion.setHasAcceptedAnswer(true);
        questionService.save(foundQuestion);

        // update answer author's badge
        User answerAuthor = foundAnswer.getAuthor();
        assignBadge(answerAuthor);
        userService.save(answerAuthor);
    }

    public void voteAnswer(String token, NewVoteRequest voteRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // check if vote value is 0
        if(voteRequest.getValue() == 0)
            throw new InvalidInputException("Please choose between 1 and -1");

        // find answer
        Answer foundAnswer = getAnswerById(voteRequest.getAnswerId());

        // check if the voting user is the answer's author
        if(Objects.equals(sessionUser, foundAnswer.getAuthor()))
            throw new AccessDeniedException("You cannot vote for your own answer");

        // instantiate vote
        Vote newVote = new Vote(voteRequest);

        // update user's vote on the answer if the user has already voted, or create a new vote
        Vote existingVote = voteService.getVoteByUserIdAndAnswerId(sessionUser.getId(), voteRequest.getAnswerId());
        if(existingVote != null){
            if(existingVote.getValue() < 0 && newVote.getValue() > 0) {
                foundAnswer.setUpVote(foundAnswer.getUpVote() + 1);
                foundAnswer.setDownVote(foundAnswer.getDownVote() - 1);
            }
            else if(existingVote.getValue() > 0 && newVote.getValue() < 0) {
                foundAnswer.setUpVote(foundAnswer.getUpVote() - 1);
                foundAnswer.setDownVote(foundAnswer.getDownVote() + 1);
            }
            newVote = existingVote;
            newVote.setValue(voteRequest.getValue());
            newVote.setLastModified(LocalDateTime.now());
        }else{
            newVote.setUser(sessionUser);
            newVote.setSubmittedDate(LocalDateTime.now());
            if(newVote.getValue() > 0) foundAnswer.setUpVote(foundAnswer.getUpVote() + 1);
            else foundAnswer.setDownVote(foundAnswer.getDownVote() + 1);
        }

        // save vote
        voteService.save(newVote);

        // update and save found answer
        foundAnswer.setScore(foundAnswer.getUpVote() - foundAnswer.getDownVote());
        answerRepo.save(foundAnswer);
    }

    public List<AnswerResponse> getAllAnswersByQuestionId(String questionId){

        Question foundQuestion = questionService.getQuestionById(questionId);
        return foundQuestion.getAnswers()
                .stream()
                .map(AnswerResponse::new)
                .collect(Collectors.toList());
    }
}
