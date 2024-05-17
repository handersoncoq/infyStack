package com.devcommunity.infyStack.models.entities;

import com.devcommunity.infyStack.dtos.requests.NewVoteRequest;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String answerId;
    @ManyToOne
    @ToString.Exclude
    private User user;
    private Integer value;
    private LocalDateTime submittedDate;
    private LocalDateTime lastModified;

    public Vote(NewVoteRequest newVoteRequest) {
        this.answerId = newVoteRequest.getAnswerId();
        this.value = newVoteRequest.getValue();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Vote vote = (Vote) o;
        return id != null && Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
