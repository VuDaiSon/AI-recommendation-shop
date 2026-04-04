package com.example.recommendershop.repository;

import com.example.recommendershop.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UUID>, JpaSpecificationExecutor<UserGroup> {
    UserGroup findByName(String name);
    Set<UserGroup> findByUsers_UserId(UUID userId);
}
