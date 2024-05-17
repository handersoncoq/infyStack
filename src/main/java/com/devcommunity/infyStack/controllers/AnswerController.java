package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.dtos.responses.AnswerResponse;
import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.dtos.requests.NewAnswerRequest;
import com.devcommunity.infyStack.dtos.requests.NewVoteRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateAnswerRequest;
import com.devcommunity.infyStack.services.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/submit")
    public ResponseEntity<?> answerQuestion(@Valid @RequestHeader("Authorization") String token,
                                               @RequestBody NewAnswerRequest answerRequest,
                                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(answerService.answerQuestion(token, answerRequest));
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptAnswer(@RequestHeader("Authorization") String token, @RequestParam String answerId){
        answerService.acceptAnswer(token, answerId);
        return ResponseEntity.ok(
                new AnswerResponse(answerService.getAnswerById(answerId))
        );
    }

    @PostMapping("/vote")
    public ResponseEntity<?> voteAnswer(@Valid @RequestHeader("Authorization") String token,
                                          @RequestBody NewVoteRequest voteRequest,
                                          BindingResult bindingResult
                                          ){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        answerService.voteAnswer(token, voteRequest);
        return ResponseEntity.ok(
                new AnswerResponse(answerService.getAnswerById(voteRequest.getAnswerId()))
        );
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateAnswer(@Valid @RequestHeader("Authorization") String token,
                                          @RequestBody UpdateAnswerRequest updateRequest,
                                          BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        answerService.updateAnswer(token, updateRequest);
        return ResponseEntity.ok(
                new AnswerResponse(answerService.getAnswerById(updateRequest.getAnswerId()))
        );
    }

    @PublicAccess
    @GetMapping("/getAllByQuestionId")
    public ResponseEntity<?> getAllAnswersByQuestionId( @RequestParam String questionId){
        return ResponseEntity.ok(answerService.getAllAnswersByQuestionId(questionId));
    }
}
