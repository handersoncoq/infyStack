package com.devcommunity.infyStack.services;

import com.devcommunity.infyStack.dtos.requests.NewBadgeRequest;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.exceptions.ResourceNotFoundException;
import com.devcommunity.infyStack.exceptions.ResourcePersistenceException;
import com.devcommunity.infyStack.models.entities.Badge;
import com.devcommunity.infyStack.repositories.BadgeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BadgeService {

    private final BadgeRepository badgeRepo;

    public Badge saveBadge(NewBadgeRequest newBadgeRequest) throws ResourcePersistenceException {

        if(badgeRepo.findByName(newBadgeRequest.getName()).isPresent())
            throw new ResourcePersistenceException("Badge already exists");

        if(newBadgeRequest.getNumberOfQuestionRequired() != 0 &&
                badgeRepo.findByNumberOfQuestionRequired(newBadgeRequest.getNumberOfQuestionRequired()) != null)
            throw new ResourcePersistenceException("This number of question required is already assigned to another badge");

        if(newBadgeRequest.getNumberOfAcceptedAnsweredRequired() != 0 &&
                badgeRepo.findByNumberOfAcceptedAnsweredRequired(newBadgeRequest.getNumberOfAcceptedAnsweredRequired()) != null)
            throw new ResourcePersistenceException("This number of accepted answer required is already assigned to another badge");

        return badgeRepo.save(new Badge(newBadgeRequest));
    }

    public Badge getBadgeByNameIgnoreCase(String name){
        return badgeRepo.findByNameEqualsIgnoreCase(name);
    }

    public Badge getDefaultBadge(UserRole role){

        Badge defaultUserBadge;

        if(role == UserRole.USER) defaultUserBadge = getBadgeByNameIgnoreCase("NOVICE");
        else defaultUserBadge = getBadgeByNameIgnoreCase("ADMIN");

        if(defaultUserBadge == null) defaultUserBadge = makeDefaultBadge(role);

        return badgeRepo.save(defaultUserBadge);
    }

    public Badge getBadgeById(Integer id){
        return badgeRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Badge does not exist"));
    }

    public Badge makeDefaultBadge(UserRole role){

        Badge badge = new Badge();
        String name, description, criteria;

        if(role == UserRole.USER){
            name = "NOVICE";
            description = "default badge for new users";
            criteria = "new user";
        }else{
            name = "ADMIN";
            description = "default badge for admins";
            criteria = "admin";
        }

        badge.setName(name);
        badge.setDescription(description);
        badge.setCriteria(criteria);
        badge.setNumberOfQuestionRequired(0);
        badge.setNumberOfAcceptedAnsweredRequired(0);

        return badgeRepo.save(badge);
    }

    public Badge getByNumberOfQuestionRequired(Integer numberOfQuestion){
        return badgeRepo.findByNumberOfQuestionRequired(numberOfQuestion);
    }

    public Badge getByNumberOfAcceptedAnsweredRequired(Integer numberOfAnswered){
        return badgeRepo.findByNumberOfAcceptedAnsweredRequired(numberOfAnswered);
    }
}
