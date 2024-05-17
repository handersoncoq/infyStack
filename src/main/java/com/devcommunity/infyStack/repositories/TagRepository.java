package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(@NonNull String name);


}