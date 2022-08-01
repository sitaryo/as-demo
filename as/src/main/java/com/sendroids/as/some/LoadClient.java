package com.sendroids.as.some;

import com.sendroids.as.service.JpaRegisteredClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class LoadClient {

    final JpaRegisteredClientRepository clientRepository;

    public LoadClient(JpaRegisteredClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @EventListener(classes = ApplicationStartedEvent.class)
    public void loadDefaultClient() {
        if(!clientRepository.findAll().isEmpty()){
            log.warn("ship load default client");
            return;
        }

        List.of(
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client")
                        .clientSecret("{noop}licky-password")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://client.localhost:9090/authorized")
                        .scope("read")
                        .scope("write")
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client-oidc")
                        .clientSecret("{noop}licky-oidc-password")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://client.localhost:9090/login/licky-client-oidc")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .scope(OidcScopes.EMAIL)
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client-credentials")
                        .clientSecret("{noop}licky-credentials-password")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .redirectUri("http://client.localhost:9090/credentials")
                        .scope("read")
                        .scope("write")
                        .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-public")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://127.0.0.1:8989/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .build()
        ).forEach(clientRepository::save);
    }
}
