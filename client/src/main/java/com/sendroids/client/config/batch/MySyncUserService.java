package com.sendroids.client.config.batch;

import com.sendroids.client.entity.UserEntity;
import com.sendroids.usersync.client.SyncUserService;
import com.sendroids.usersync.core.converter.ToUserIdentity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MySyncUserService extends SyncUserService<UserEntity> {

    public MySyncUserService(
            ToUserIdentity<UserEntity> toUserIdentity,
            ClientRegistrationRepository clientRegistrationRepository,
            WebClient webClient
    ) {
        super(toUserIdentity, clientRegistrationRepository, webClient);
    }
}
