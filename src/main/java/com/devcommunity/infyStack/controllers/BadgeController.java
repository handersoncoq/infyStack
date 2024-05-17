package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.UserAccess;
import com.devcommunity.infyStack.dtos.requests.NewBadgeRequest;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.services.BadgeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@AllArgsConstructor
@RequestMapping("/badge")
public class BadgeController {

    private final BadgeService badgeService;

    @UserAccess({UserRole.ADMIN})
    @PostMapping("/addBadge")
    public ResponseEntity<?> addBadge(@Valid @RequestBody NewBadgeRequest newBadgeRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.ok(badgeService.saveBadge(newBadgeRequest));
    }
}
