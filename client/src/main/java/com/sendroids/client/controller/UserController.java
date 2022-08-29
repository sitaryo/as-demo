package com.sendroids.client.controller;

import com.sendroids.client.config.batch.MySyncUserService;
import com.sendroids.client.entity.Authority;
import com.sendroids.client.entity.UserEntity;
import com.sendroids.client.entity.UserProfile;
import com.sendroids.client.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// todo add api
@RestController
@RequestMapping("/users")
public class UserController {
    private final MySyncUserService syncUserService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public UserController(
            MySyncUserService syncUserService,
            PasswordEncoder passwordEncoder,
            UserRepo userRepo
    ) {
        this.syncUserService = syncUserService;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @PostMapping
    public void registerUser() {

        var user = new UserEntity();
        user.setUsername("user-api-" + LocalDateTime.now());
        user.setPassword(passwordEncoder.encode("user-api"));
        user.addAuthority(new Authority(Authority.ROLE.CLIENT_USER));
        var userProfile = new UserProfile();
        userProfile.setEmail("user-email-api-" + LocalDateTime.now());
        userProfile.setEmailVerified(true);
        userProfile.setPhoneNumber("user-phoneNumber-api-" + LocalDateTime.now());
        userProfile.setPhoneNumberVerified(true);
        user.setUserProfile(userProfile);
        userRepo.save(user);
        var result = syncUserService.createUser(user);
        System.out.println(result.getUnionId());
    }

    @PutMapping
    public void updateUser() {
        userRepo.findByUsername("user1")
                .ifPresent(user->{
                    user.setPassword(passwordEncoder.encode("re-password"));
                    user.getUserProfile().setEmail("re-email");
                    user.getUserProfile().setPhoneNumber("re-phoneNumber");
                    var result = syncUserService.updateUser(user);
                    System.out.println(result);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteUser(String id) {
        syncUserService.deleteUser(id);
    }
}
