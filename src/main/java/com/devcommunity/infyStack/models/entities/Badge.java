package com.devcommunity.infyStack.models.entities;

import com.devcommunity.infyStack.dtos.requests.NewBadgeRequest;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String criteria;
    private Integer numberOfQuestionRequired;
    private Integer numberOfAcceptedAnsweredRequired;

    public Badge(NewBadgeRequest newBadgeRequest) {
        this.name = newBadgeRequest.getName().strip().toUpperCase();
        this.description = newBadgeRequest.getDescription();
        this.criteria = newBadgeRequest.getCriteria();
        this.numberOfQuestionRequired = newBadgeRequest.getNumberOfQuestionRequired();
        this.numberOfAcceptedAnsweredRequired = newBadgeRequest.getNumberOfAcceptedAnsweredRequired();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Badge badge = (Badge) o;
        return id != null && Objects.equals(id, badge.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
