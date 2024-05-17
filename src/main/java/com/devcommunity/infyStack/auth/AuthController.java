package com.devcommunity.infyStack.auth;

import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.annotations.UserAccess;
import com.devcommunity.infyStack.dtos.requests.NewUserRequest;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.exceptions.AuthenticationException;
import com.devcommunity.infyStack.exceptions.InvalidInputException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PublicAccess
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            throw new InvalidInputException(errorMessage.toString());
        }
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PublicAccess
    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@Valid @RequestBody NewUserRequest newUserRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        authService.processNewUserRequest(newUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully, verification email sent.");
    }

    @PublicAccess
    @GetMapping("/verifyEmail")
    public ResponseEntity<?> verifyUserEmail(@RequestParam String token) {
        authService.verifyUserEmail(token);
        return ResponseEntity.ok("Email verified successfully.");
    }


    @UserAccess({UserRole.ADMIN})
    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody NewUserRequest newUserRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.processNewAdminRequest(newUserRequest));
    }

    @PublicAccess
    @PatchMapping("/updatePassword")
    public ResponseEntity<AuthenticationResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updateRequest,
                                                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()){
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
            throw new InvalidInputException(errorMessage.toString());
        }
        return ResponseEntity.ok(authService.updatePassword(updateRequest));
    }
}
