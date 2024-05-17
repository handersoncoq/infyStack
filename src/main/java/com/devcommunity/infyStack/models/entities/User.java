package com.devcommunity.infyStack.models.entities;

import com.devcommunity.infyStack.dtos.requests.NewUserRequest;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"password"})

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String pseudoName;

    private String password;
    @ElementCollection
    private List<String> techStack;
    private String profileUrl;

    @ManyToOne
    private Badge badge;
    @ElementCollection
    private List<String> questionIds;
    @ElementCollection
    private List<String> answerIds;
    @ElementCollection
    private List<String> commentIds;
    @ElementCollection
    private List<String> replyIds;
    @ElementCollection
    private List<String> questionViewedIds;
    @ElementCollection
    private List<String> commentLikedIds;
    @ElementCollection
    private List<String> replyLikedIds;

    private LocalDate joinDate;
    @Enumerated(value = EnumType.STRING)
    private UserRole role;
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;
    private String verificationToken;
    private boolean emailVerified = false;

    public User(NewUserRequest newUserRequest){
        this.email = newUserRequest.getEmail();
        this.pseudoName = newUserRequest.getPseudoName();
        this.firstName = newUserRequest.getFirstName();
        this.lastName = newUserRequest.getLastName();
        this.techStack = newUserRequest.getTechStack();
        this.questionIds = new ArrayList<>();
        this.answerIds = new ArrayList<>();
        this.commentIds = new ArrayList<>();
        this.replyIds = new ArrayList<>();
        this.questionViewedIds = new ArrayList<>();
        this.commentLikedIds = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return userStatus != UserStatus.CLOSED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userStatus != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userStatus == UserStatus.ACTIVE;
    }
}
