package com.sendroids.resource.controller;

import com.nimbusds.oauth2.sdk.ParseException;
import com.sendroids.resource.util.OAuth2Util;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@RestController
@RequestMapping("/oidc")
public class OidcClientController {
    private final WebClient webClient;

    public OidcClientController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/message")
    public String messageOidc(@RegisteredOAuth2AuthorizedClient("licky-client-oidc") OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri("http://rescource.localhost:7070/message")
                .attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/logout")
    public int logoutOidc(@RegisteredOAuth2AuthorizedClient("licky-client-oidc") OAuth2AuthorizedClient client) throws IOException {
        return OAuth2Util.logout(client);
    }

    @GetMapping("/register-client")
    public String registerClient() throws IOException, ParseException {
        return OAuth2Util.registerClient();
    }

    @GetMapping("/client/{clientId}")
    public String getClientInfo(
            @PathVariable String clientId
    ) throws IOException, ParseException {
        return OAuth2Util.getClientInfo(clientId);
    }
}
