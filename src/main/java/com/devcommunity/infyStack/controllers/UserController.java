package com.devcommunity.infyStack.controllers;

import com.devcommunity.infyStack.annotations.UserAccess;
import com.devcommunity.infyStack.dtos.responses.UserResponse;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", methods = { GET, POST, PUT, DELETE }, allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @UserAccess({UserRole.ADMIN})
    @GetMapping("/getByEmail")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(new UserResponse(userService.getByEmail(email)));
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token){
        return ResponseEntity.ok(new UserResponse(userService.getByToken(token)));
    }
}
