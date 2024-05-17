package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.dtos.requests.NewCommentRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateCommentRequest;
import com.devcommunity.infyStack.dtos.responses.CommentResponse;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.models.documents.Answer;
import com.devcommunity.infyStack.models.documents.Comment;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final AnswerService answerService;
    private final UserService userService;

    public CommentResponse postComment(String token, NewCommentRequest newCommentRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find answer
        Answer foundAnswer = answerService.getAnswerById(newCommentRequest.getAnswerId());

        // instantiate new comment
        Comment newComment = new Comment();
        newComment.setBody(newCommentRequest.getBody());
        newComment.setAuthor(sessionUser);
        newComment.setAnswer(foundAnswer);
        newComment.setDateCreated(LocalDateTime.now());
        commentRepo.save(newComment);

        // add comment to answer's list of comments
        foundAnswer.getCommentIds().add(newComment.getId());
        answerService.save(foundAnswer);

        // add comment to user's list of comments
        sessionUser.getCommentIds().add(newComment.getId());
        userService.save(sessionUser);

        return new CommentResponse(newComment);
    }

    public void updateComment(String token, UpdateCommentRequest updateRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // make sure user is editing their own comment
        if(!sessionUser.getCommentIds().contains(updateRequest.getCommentId()))
            throw new AccessDeniedException("You cannot modify this comment");

        // find comment and update if necessary
        Comment foundComment = getCommentById(updateRequest.getCommentId());
        if(updateRequest.getBody() != null && !updateRequest.getBody().strip().equals("")){
            foundComment.setBody(updateRequest.getBody());
            foundComment.setLastEdited(LocalDateTime.now());
        }
        commentRepo.save(foundComment);
    }

    public void likeOrUnlikeComment(String token, String commentId){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find comment
        Comment foundComment = getCommentById(commentId);

        // if comment exists in user's liked comment list, remove the like, else add
        if(sessionUser.getCommentLikedIds().contains(commentId)){
            foundComment.setLikes(foundComment.getLikes() - 1);
            List<String> modifiedLikeList = sessionUser.getCommentLikedIds();
            modifiedLikeList.remove(commentId);
            sessionUser.setCommentLikedIds(modifiedLikeList);
        }else{
            foundComment.setLikes(foundComment.getLikes() + 1);
            sessionUser.getCommentLikedIds().add(commentId);
        }

        // persist
        commentRepo.save(foundComment);
        userService.save(sessionUser);
    }

    public Comment getCommentById(String id){
        return commentRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment does not exist")
        );
    }

    public CommentResponse getByCommentId(String id){
        return new CommentResponse(getCommentById(id));
    }

    public void save(Comment comment){
        commentRepo.save(comment);
    }

    public void deleteComment(String token, String commentId){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find and delete comment
        Comment foundComment = getCommentById(commentId);
        commentRepo.delete(foundComment);

        // remove comment from user's comment list
        List<String> modifyingCommentIds = sessionUser.getCommentIds();
        modifyingCommentIds.remove(commentId);
        sessionUser.setCommentIds(modifyingCommentIds);
        userService.save(sessionUser);
    }

    public List<CommentResponse> getAllCommentsByAnswerId(String answerId){
        Answer foundAnswer = answerService.getAnswerById(answerId);
        return foundAnswer.getCommentIds()
                .stream()
                .map(this::getByCommentId)
                .collect(Collectors.toList());
    }
}
