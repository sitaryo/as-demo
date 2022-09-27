package com.sendroids.client.init;

import com.sendroids.client.entity.Authority;
import com.sendroids.client.entity.UserEntity;
import com.sendroids.client.entity.UserProfile;
import com.sendroids.client.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class LoadDefaultData {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public LoadDefaultData(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(classes = ApplicationStartedEvent.class)
    public void LoadData() {
        loadDefaultUser();
    }

    private UserEntity createUser(int i) {
        var user = new UserEntity();
        user.setUsername("user-" + i);
        user.setPassword(passwordEncoder.encode("user-" + i));
        user.addAuthority(new Authority(Authority.ROLE.CLIENT_USER));
        var userProfile = new UserProfile();
        userProfile.setEmail("user-email-" + i);
        userProfile.setEmailVerified(true);
        userProfile.setPhoneNumber("user-phoneNumber-" + i);
        userProfile.setPhoneNumberVerified(true);
        user.setUserProfile(userProfile);
        return user;
    }

    private void loadDefaultUser() {
        if (!userRepo.findAll().isEmpty()) {
            log.warn("skip load default users");
            return;
        }

        userRepo.saveAll(
                IntStream.range(1, 3)
                        .mapToObj(this::createUser)
                        .collect(Collectors.toList())
        );
    }
}
