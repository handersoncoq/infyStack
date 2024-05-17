package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.dtos.requests.NewQuestionRequest;
import com.devcommunity.infyStack.dtos.requests.UpdateQuestionRequest;
import com.devcommunity.infyStack.dtos.responses.QuestionResponse;
import com.devcommunity.infyStack.services.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@Valid @RequestHeader("Authorization") String token,
                                         @RequestBody NewQuestionRequest newQuestionRequest,
                                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(questionService.askQuestion(token, newQuestionRequest));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateQuestion(@Valid @RequestHeader("Authorization") String token,
                                         @RequestBody UpdateQuestionRequest updateRequest,
                                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        questionService.updateQuestion(token, updateRequest);
        return ResponseEntity.ok(
                new QuestionResponse(questionService.getQuestionById(updateRequest.getQuestionId()))
        );
    }

    @PublicAccess
    @GetMapping("/getByTagsFromRequest")
    public ResponseEntity<?> getSimilarQuestionsBasedOnRequestTags(@Valid @RequestBody
                                                                   NewQuestionRequest newQuestionRequest,
                                                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(
                questionService.getSimilarQuestionsBasedOnRequestTags(newQuestionRequest)
        );
    }

    @PublicAccess
    @GetMapping("/getById")
    public ResponseEntity<?> getQuestionById(@RequestParam String id){
        return ResponseEntity.ok( new QuestionResponse(questionService.getQuestionById(id)));
    }

    @PublicAccess
    @GetMapping("/getByTag")
    public ResponseEntity<?> getQuestionsByTag(@RequestParam String name){
        return ResponseEntity.ok(questionService.getQuestionsByTag(name));
    }

    @PublicAccess
    @GetMapping("/view")
    public ResponseEntity<?> viewQuestion(@RequestHeader("Authorization") @Nullable String token,
                                          @RequestParam String questionId){
        questionService.viewQuestion(token, questionId);
        return ResponseEntity.ok(new QuestionResponse(questionService.getQuestionById(questionId)));
    }

    @PublicAccess
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllQuestions(){
        return ResponseEntity.ok(questionService.getQuestionsByPage());
    }

    @PublicAccess
    @GetMapping("/fetch")
    public ResponseEntity<Page<QuestionResponse>> fetchQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<QuestionResponse> pageQuestions = questionService.getQuestionsByPage(page, size);
        return new ResponseEntity<>(pageQuestions, HttpStatus.OK);
    }

}
