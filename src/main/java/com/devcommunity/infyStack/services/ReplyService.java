package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.dtos.requests.NewReplyRequest;
import com.devcommunity.infyStack.dtos.responses.ReplyResponse;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.dtos.requests.UpdateReplyRequest;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import com.devcommunity.infyStack.models.documents.Comment;
import com.devcommunity.infyStack.models.documents.Reply;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.repositories.ReplyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepo;
    private final CommentService commentService;
    private final UserService userService;

    public ReplyResponse postReply(String token, NewReplyRequest newReplyRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find comment
        Comment foundComment = commentService.getCommentById(newReplyRequest.getCommentId());

        // instantiate new reply
        Reply newReply = new Reply();
        newReply.setBody(newReplyRequest.getBody());
        newReply.setComment(foundComment);
        newReply.setAuthor(sessionUser);
        newReply.setDateCreated(LocalDateTime.now());
        replyRepo.save(newReply);

        // add reply to comment's list of replies
        foundComment.getReplyIds().add(newReply.getId());
        commentService.save(foundComment);

        // add reply to user's list of replies
        sessionUser.getReplyIds().add(newReply.getId());
        userService.save(sessionUser);

        return new ReplyResponse(newReply);
    }

    public void updateReply(String token, UpdateReplyRequest updateRequest){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // make sure user is editing their own reply
        if(!sessionUser.getReplyIds().contains(updateRequest.getReplyId()))
            throw new AccessDeniedException("You cannot modify this reply");

        // find reply and update if necessary
        Reply foundReply = getReplyById(updateRequest.getReplyId());
        if(updateRequest.getBody() != null && !updateRequest.getBody().strip().equals("")){
            foundReply.setBody(updateRequest.getBody());
            foundReply.setLastEdited(LocalDateTime.now());
        }
        replyRepo.save(foundReply);
    }

    public void likeOrUnlikeReply(String token, String replyId){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find reply
        Reply foundReply = getReplyById(replyId);

        // if reply exists in user's liked reply list, remove the like, else add
        if(sessionUser.getReplyLikedIds().contains(replyId)){
            foundReply.setLikes(foundReply.getLikes() - 1);
            List<String> modifiedLikeList = sessionUser.getReplyLikedIds();
            modifiedLikeList.remove(replyId);
            sessionUser.setReplyLikedIds(modifiedLikeList);
        }else{
            foundReply.setLikes(foundReply.getLikes() + 1);
            sessionUser.getReplyLikedIds().add(replyId);
        }

        // persist
        replyRepo.save(foundReply);
        userService.save(sessionUser);
    }

    public void deleteReply(String token, String replyId){

        // authenticate user
        User sessionUser = userService.getByToken(token);

        // find reply
        Reply foundReply = getReplyById(replyId);
        replyRepo.delete(foundReply);

        // remove reply from user's reply list
        List<String> modifyingReplyIds = sessionUser.getReplyIds();
        modifyingReplyIds.remove(replyId);
        sessionUser.setReplyIds(modifyingReplyIds);
        userService.save(sessionUser);
    }

    public Reply getReplyById(String id){
        return replyRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Reply does not exist")
        );
    }

    public ReplyResponse getByReplyId(String id){
        return new ReplyResponse(getReplyById(id));
    }

    public List<ReplyResponse> getAllRepliesByCommentId(String commentId){
        Comment foundComment = commentService.getCommentById(commentId);
        return foundComment.getReplyIds()
                .stream()
                .map(this::getByReplyId)
                .collect(Collectors.toList());
    }
}
