package com.sendroids.resource.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

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
}
