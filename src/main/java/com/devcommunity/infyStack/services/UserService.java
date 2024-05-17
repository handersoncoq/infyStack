package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.models.entities.User;
import com.devcommunity.infyStack.repositories.UserRepository;
import com.devcommunity.infyStack.configs.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final TokenService tokenService;


    public User getByToken(String token) {
        if (token.startsWith("Bearer ")) token = token.substring(7);
        return getByEmail(tokenService.extractEmail(token));
    }

    public User getByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email)
                .orElseThrow( ()-> new ResourceNotFoundException("Invalid email address."));
    }

    public boolean existsByEmail(String email) {
        return userRepo.findByEmailIgnoreCase(email).isPresent();
    }

    public void save(User user){
        userRepo.save(user);
    }

}
