package com.devcommunity.infyStack.auth;


import com.devcommunity.infyStack.dtos.requests.NewUserRequest;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import com.devcommunity.infyStack.exceptions.AuthenticationException;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.exceptions.ResourcePersistenceException;
import com.devcommunity.infyStack.enums.UserStatus;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.repositories.UserRepository;
import com.devcommunity.infyStack.configs.security.TokenService;
import com.devcommunity.infyStack.services.BadgeService;
import com.devcommunity.infyStack.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final BadgeService badgeService;
    private final EmailService emailService;

    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword()));
        User user = userRepo.findByEmailIgnoreCase(authRequest.getEmail())
                .orElseThrow( ()-> new ResourceNotFoundException("Email not registered"));
        if(!user.isEmailVerified()) throw new AuthenticationException("Please verify your email address.");
        String token = tokenService.generateToken(user);
        Date tokenExpiryDate = tokenService.extractExpiration(token);
        return new AuthenticationResponse(
                token,
                user.getRole(),
                user.getUserStatus(),
                tokenExpiryDate
        );
    }

    public void processNewUserRequest(NewUserRequest newUserRequest){
        User newUser = saveUser(newUserRequest, UserRole.USER);
        emailService.sendVerificationEmail(newUser.getEmail(), newUser.getVerificationToken());

    }

    public void verifyUserEmail(String verificationToken) {
        userRepo.findByVerificationToken(verificationToken)
                .ifPresentOrElse(user -> {
                    if (!user.isEmailVerified()) {
                        user.setEmailVerified(true);
                        userRepo.save(user);
                    }else throw new AuthenticationException("Email has already been verified.");
                }, () -> {
                    throw new AuthenticationException("Invalid verification token.");
                });
    }

    public AuthenticationResponse processNewAdminRequest(NewUserRequest newUserRequest){
        User newAdmin = saveUser(newUserRequest, UserRole.ADMIN);
        String token = tokenService.generateToken(newAdmin);
        Date tokenExpiryDate = tokenService.extractExpiration(token);
        return new AuthenticationResponse(
                token,
                newAdmin.getRole(),
                newAdmin.getUserStatus(),
                tokenExpiryDate
        );
    }

    public User saveUser(NewUserRequest newUserRequest, UserRole role) throws AccessDeniedException, ResourcePersistenceException {

        if(userRepo.findByEmailIgnoreCase(newUserRequest.getEmail()).isPresent()) {
            throw new ResourcePersistenceException("Email is already registered. Please log in.");
        }

        if(userRepo.findByPseudoNameIgnoreCase(newUserRequest.getPseudoName()).isPresent()) {
            throw new ResourcePersistenceException("Pseudo name already exists. Please choose another pseudo name.");
        }

        User user = new User(newUserRequest);
        user.setPassword(passwordEncoder.encode(newUserRequest.getPassword()));
        user.setRole(role);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setProfileUrl("");
        user.setBadge(badgeService.getDefaultBadge(role));
        user.setJoinDate(LocalDate.now());
        user.setVerificationToken(generateVerificationToken());
        userRepo.save(user);
        return user;
    }

    public AuthenticationResponse updatePassword(UpdatePasswordRequest updateRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                updateRequest.getEmail(), updateRequest.getOldPassword()));
        User authUser = userRepo.findByEmailIgnoreCase(updateRequest.getEmail())
                .orElseThrow( ()-> new ResourceNotFoundException("Email not registered"));
        authUser.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
        userRepo.save(authUser);

        String token = tokenService.generateToken(authUser);
        Date tokenExpiryDate = tokenService.extractExpiration(token);
        return new AuthenticationResponse(
                token,
                authUser.getRole(),
                authUser.getUserStatus(),
                tokenExpiryDate
        );
    }

    public String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}
