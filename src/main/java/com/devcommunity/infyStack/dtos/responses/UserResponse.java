package com.devcommunity.infyStack.dtos.responses;

import com.devcommunity.infyStack.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private List<String> techStack;
    private String badge;
    private String profileUrl;
    private LocalDate joinDate;

    public UserResponse(User user) {
        this.username = user.getPseudoName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.role = user.getRole().toString();
        this.techStack = user.getTechStack();
        this.badge = user.getBadge().getName();
        this.profileUrl = user.getProfileUrl();
        this.joinDate = user.getJoinDate();
    }
}
