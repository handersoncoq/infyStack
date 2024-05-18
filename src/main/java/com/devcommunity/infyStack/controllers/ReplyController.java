package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.dtos.requests.NewReplyRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateReplyRequest;
import com.devcommunity.infyStack.dtos.responses.ReplyResponse;
import com.devcommunity.infyStack.services.ReplyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@RestController
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/post")
    public ResponseEntity<?> postReply(@Valid @RequestHeader("Authorization") String token,
                                         @RequestBody NewReplyRequest replyRequest,
                                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(replyService.postReply(token, replyRequest));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateReply(@Valid @RequestHeader("Authorization") String token,
                                           @RequestBody UpdateReplyRequest updateRequest,
                                           BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        replyService.updateReply(token, updateRequest);
        return ResponseEntity.ok(
                new ReplyResponse(replyService.getReplyById(updateRequest.getReplyId()))
        );
    }

    @PostMapping("/likeOrUnlike")
    public ResponseEntity<?> likeOrUnlikeReply(@RequestHeader("Authorization") String token,
                                                 @RequestParam String replyId){
        replyService.likeOrUnlikeReply(token, replyId);
        return ResponseEntity.ok(
                new ReplyResponse(replyService.getReplyById(replyId))
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReply(@RequestHeader("Authorization") String token,
                                           @RequestParam String replyId){
        replyService.deleteReply(token, replyId);
        return ResponseEntity.ok("Your reply was successfully removed.");
    }

    @PublicAccess
    @GetMapping("/getAllByCommentId")
    public ResponseEntity<?> getAllRepliesByCommentId(@RequestParam String commentId){
        return ResponseEntity.ok(replyService.getAllRepliesByCommentId(commentId));
    }
}
