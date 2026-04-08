package com.example.recommendershop.repository;

import com.example.recommendershop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    User findByName(String name);
    User getUserByEmail(String email);
    Optional<User> findByEmail(String email);
    User getByUserId(UUID userId);
    Optional<User> findByResetToken(String token);

}
