package com.sendroids.as.init;

import com.sendroids.as.entity.Authority;
import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.entity.UserProfile;
import com.sendroids.as.repo.UserRepo;
import com.sendroids.as.service.JpaRegisteredClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class LoadDefaultData {

    private final JpaRegisteredClientRepository clientRepository;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public LoadDefaultData(
            JpaRegisteredClientRepository clientRepository,
            UserRepo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.clientRepository = clientRepository;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(classes = ApplicationStartedEvent.class)
    public void LoadData() {
        loadDefaultUser();
        loadDefaultClient();
    }

    private UserEntity createUser(int i) {
        var user = new UserEntity();
        user.setUsername("user" + i);
        user.setPassword(passwordEncoder.encode("user" + i));
        user.addAuthority(new Authority(Authority.ROLE.USER));
        var userProfile = new UserProfile();
        userProfile.setAddress("user-address-" + i);
        userProfile.setBirthdate("user-birthdate-" + i);
        userProfile.setEmail("user-email-" + i);
        userProfile.setEmailVerified(true);
        userProfile.setBirthdate("user-birthdate-" + i);
        userProfile.setFamilyName("user-familyName-" + i);
        userProfile.setGender("user-gender-" + i);
        userProfile.setGivenName("user-givenName-" + i);
        userProfile.setLocale("user-locale-" + i);
        userProfile.setMiddleName("user-middleName-" + i);
        userProfile.setName("user-name-" + i);
        userProfile.setNickname("user-nickname-" + i);
        userProfile.setPicture("user-picture-" + i);
        userProfile.setPhoneNumber("user-phoneNumber-" + i);
        userProfile.setPhoneNumberVerified(true);
        userProfile.setPreferredUsername("user-preferredUsername-" + i);
        userProfile.setProfile("user-profile-" + i);
        userProfile.setUpdatedAt("user-updatedAt-" + i);
        userProfile.setWebsite("user-website-" + i);
        userProfile.setZoneinfo("user-zoneinfo-" + i);
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

    private void loadDefaultClient() {
        if (!clientRepository.findAll().isEmpty()) {
            log.warn("skip load default client");
            return;
        }

        List.of(
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client")
                        .clientSecret(passwordEncoder.encode("licky-password"))
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://client.localhost:9090/client/authorized")
                        .scope("read")
                        .scope("write")
                        .clientIdIssuedAt(Instant.now())
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client-oidc")
                        .clientSecret(passwordEncoder.encode("licky-oidc-password"))
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://client.localhost:9090/oidc/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .scope(OidcScopes.EMAIL)
                        .scope(OidcScopes.ADDRESS)
                        .scope(OidcScopes.PHONE)
                        .scope(OidcScopes.PROFILE)
                        .clientIdIssuedAt(Instant.now())
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client-credentials")
                        .clientSecret(passwordEncoder.encode("licky-credentials-password"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .redirectUri("http://client.localhost:9090/credentials")
                        .scope("read")
                        .scope("write")
                        .scope("user.register")
                        .scope("user.update")
                        .scope("user.delete")
                        .clientIdIssuedAt(Instant.now())
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("dev-client")
                        .clientSecret(passwordEncoder.encode("dev-client-password"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .redirectUri("http://client.localhost:9090/oidc/dev-client")
                        .scope("client.create")
                        .scope("client.read")
                        .clientIdIssuedAt(Instant.now())
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-public")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://192.168.3.137:8989/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .scope(OidcScopes.EMAIL)
                        .clientIdIssuedAt(Instant.now())
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("resource-client")
                        .clientSecret(passwordEncoder.encode("resource-client-password"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .clientIdIssuedAt(Instant.now())
                        .build()
        ).forEach(clientRepository::save);
    }
}
