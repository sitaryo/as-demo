package com.sendroids.usersync.client;

import com.sendroids.usersync.core.converter.ToUserIdentity;
import com.sendroids.usersync.core.entity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Objects;

@Slf4j
public abstract class SyncUserService<USER> {

    private static OAuth2AccessToken accessToken;
    private final ToUserIdentity<USER> toUserIdentity;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final WebClient webClient;
    @Value("${sync.user.uri}")
    private String baseUri;
    @Value("${sync.user.client-id}")
    private String clientId;

    public SyncUserService(
            ToUserIdentity<USER> toUserIdentity,
            ClientRegistrationRepository clientRegistrationRepository,
            WebClient webClient
    ) {
        this.toUserIdentity = toUserIdentity;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.webClient = webClient;
    }

    private OAuth2AccessToken accessToken() {
        if (
                Objects.nonNull(accessToken) &&
                        Objects.nonNull(accessToken.getExpiresAt()) &&
                        accessToken.getExpiresAt().isAfter(Instant.now())
        ) {
            return accessToken;
        }
        log.trace("get access token [use to sync user info]");
        var clientRegistration = clientRegistrationRepository.findByRegistrationId(clientId);
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(
                clientRegistration);
        OAuth2AccessTokenResponse tokenResponse = new DefaultClientCredentialsTokenResponseClient().getTokenResponse(clientCredentialsGrantRequest);
        accessToken = tokenResponse.getAccessToken();

        return accessToken;
    }

    public UserIdentity updateUser(@NonNull USER user) {
        var data = toUserIdentity.convert(user);
        return webClient.put()
                .uri(baseUri)
                .headers(h -> h.setBearerAuth(accessToken().getTokenValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(UserIdentity.class)
                .block();
    }

    public UserIdentity createUser(@NonNull USER user) {
        var data = toUserIdentity.convert(user);
        return webClient.post()
                .uri(baseUri)
                .headers(h -> h.setBearerAuth(accessToken().getTokenValue()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(UserIdentity.class)
                .block();
    }

    public void deleteUser(@NonNull String unionId) {
        webClient.delete()
                .uri(baseUri + "/" + unionId)
                .headers(h -> h.setBearerAuth(accessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
