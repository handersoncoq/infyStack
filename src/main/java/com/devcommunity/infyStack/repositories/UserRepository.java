package com.devcommunity.infyStack.repositories;

import com.devcommunity.infyStack.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByPseudoNameIgnoreCase(String pseudoName);

}