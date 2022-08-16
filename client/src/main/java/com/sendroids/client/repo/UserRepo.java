package com.sendroids.client.repo;

import com.sendroids.client.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByUnionIdIsNull(Pageable pageable);

    List<UserEntity> findAllByUsernameIn(Collection<String> username);
}
