package org.example.repository;

import org.example.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByRegisterToken(String token);
    Optional<User> findByEmail(String extractedMail);
}
