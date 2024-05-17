package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.entities.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {

    @Query("select b from Badge b where upper(b.name) = upper(?1)")
    Badge findByNameEqualsIgnoreCase(@NonNull String name);
    Optional<Badge> findByName(String name);

    @Query("select b from Badge b where b.numberOfAcceptedAnsweredRequired = ?1")
    Badge findByNumberOfAcceptedAnsweredRequired(Integer numberOfAcceptedAnsweredRequired);

    @Query("select b from Badge b where b.numberOfQuestionRequired = ?1")
    Badge findByNumberOfQuestionRequired(@NonNull Integer numberOfQuestionRequired);
}