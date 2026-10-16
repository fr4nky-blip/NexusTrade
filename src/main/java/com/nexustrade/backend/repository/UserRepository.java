package com.nexustrade.backend.repository;

import com.nexustrade.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automatically writes the SQL query to find a user by email!
    Optional<User> findByEmail(String email);
}