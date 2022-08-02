package com.sendroids.resource.controller;

import com.nimbusds.oauth2.sdk.TokenRevocationRequest;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Token;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@RestController
public class MainController {
    private final WebClient webClient;

    public MainController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/message")
    public String message(@RegisteredOAuth2AuthorizedClient("licky-client") OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri("http://rescource.localhost:7070/message")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/message-oidc")
    public String messageOidc(@RegisteredOAuth2AuthorizedClient("licky-client-oidc") OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri("http://rescource.localhost:7070/message")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/authorized")
    public String authorized(@RegisteredOAuth2AuthorizedClient("licky-client") OAuth2AuthorizedClient client) {
        return client.getAccessToken().getTokenValue();
    }

    @GetMapping("/message-client")
    public String messageClient(@RegisteredOAuth2AuthorizedClient("licky-client-credentials") OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri("http://rescource.localhost:7070/message")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/logout-client")
    public int logoutClient(@RegisteredOAuth2AuthorizedClient("licky-client") OAuth2AuthorizedClient client) throws IOException {
        return logout(client);
    }

    private int logout(OAuth2AuthorizedClient client) throws IOException {
        return new TokenRevocationRequest(
                URI.create("http://auth.localhost:8080/oauth2/revoke"),
                switch (client.getClientRegistration().getClientAuthenticationMethod().getValue()) {
                    case "client_secret_post" -> new ClientSecretPost(
                            new ClientID(client.getClientRegistration().getClientId()),
                            new Secret(client.getClientRegistration().getClientSecret())
                    );
                    case "client_secret_basic" -> new ClientSecretBasic(
                            new ClientID(client.getClientRegistration().getClientId()),
                            new Secret(client.getClientRegistration().getClientSecret())
                    );
                    default -> throw new IllegalStateException("Unexpected value: " + client.getClientRegistration().getClientAuthenticationMethod().getValue());
                },
                Optional.ofNullable(client.getRefreshToken())
                        .map(refreshToken -> (Token) new RefreshToken(refreshToken.getTokenValue()))
                        .orElse(new BearerAccessToken(client.getAccessToken().getTokenValue()))
        ).toHTTPRequest().send().getStatusCode();
    }

    @GetMapping("/logout-oidc")
    public int logoutOidc(@RegisteredOAuth2AuthorizedClient("licky-client-oidc") OAuth2AuthorizedClient client) throws IOException {
        return logout(client);
    }
}
