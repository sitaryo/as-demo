package com.sendroids.as.service;

import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void save(UserEntity userEntity) {
        userRepo.save(userEntity);
    }

    public Optional<UserEntity> findUserByClientIdAndUsername(String clientId, String username) {
        return userRepo.findByClientIdAndUsername(clientId, username);
    }

    public Optional<UserEntity> findUserByUnionIdAndClientId(String unionId, String clientId) {
        return userRepo.findByUnionIdAndClientId(unionId, clientId);
    }

    public void delete(UserEntity user) {
        userRepo.delete(user);
    }
}
