package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.dtos.requests.NewCommentRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateCommentRequest;
import com.devcommunity.infyStack.dtos.responses.CommentResponse;
import com.devcommunity.infyStack.services.CommentService;
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
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post")
    public ResponseEntity<?> postComment(@Valid @RequestHeader("Authorization") String token,
                                            @RequestBody NewCommentRequest commentRequest,
                                            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(commentService.postComment(token, commentRequest));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateComment(@Valid @RequestHeader("Authorization") String token,
                                         @RequestBody UpdateCommentRequest updateRequest,
                                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        commentService.updateComment(token, updateRequest);
        return ResponseEntity.ok(
                new CommentResponse(commentService.getCommentById(updateRequest.getCommentId()))
        );
    }

    @PostMapping("/likeOrUnlike")
    public ResponseEntity<?> likeOrUnlikeComment(@RequestHeader("Authorization") String token,
                                                 @RequestParam String commentId){
        commentService.likeOrUnlikeComment(token, commentId);
        return ResponseEntity.ok(
                new CommentResponse(commentService.getCommentById(commentId))
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token,
                                                 @RequestParam String commentId){
        commentService.deleteComment(token, commentId);
        return ResponseEntity.ok("Your comment was successfully removed.");
    }

    @PublicAccess
    @GetMapping("/getAllByAnswerId")
    public ResponseEntity<?> getAllCommentsByAnswerId(@RequestParam String answerId){
        return ResponseEntity.ok(commentService.getAllCommentsByAnswerId(answerId));
    }
}
