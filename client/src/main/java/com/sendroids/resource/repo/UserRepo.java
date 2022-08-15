package com.sendroids.resource.repo;

import com.sendroids.resource.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByUnionIdIsNull(Pageable pageable);
}