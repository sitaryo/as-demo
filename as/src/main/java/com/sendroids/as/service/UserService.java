package com.sendroids.as.service;

import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void save(UserEntity userEntity){
        userRepo.save(userEntity);
    }
}
