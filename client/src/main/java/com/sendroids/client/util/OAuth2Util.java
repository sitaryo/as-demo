package com.sendroids.client.util;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientReadRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

public class OAuth2Util {

    private static final String AUTH_SERVER_URI = "http://auth.localhost:8080";
    private static final URI REVOKE_ENDPOINT = URI.create(AUTH_SERVER_URI + "/oauth2/revoke");
    private static final URI OIDC_CLIENT_REGISTRATION = URI.create(AUTH_SERVER_URI + "/connect/register");

    public static int logout(OAuth2AuthorizedClient client) throws IOException {
        return new TokenRevocationRequest(
                REVOKE_ENDPOINT,
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

    private static BearerAccessToken devClientAccessToken(Scope scope) throws IOException, ParseException {
        return AccessTokenResponse.parse(
                new TokenRequest(
                        URI.create("http://auth.localhost:8080/oauth2/token"),
                        new ClientSecretBasic(
                                new ClientID("dev-client"),
                                new Secret("dev-client-password")
                        ),
                        new ClientCredentialsGrant(),
                        scope
                )
                        .toHTTPRequest()
                        .send()
        )
                .getTokens()
                .getBearerAccessToken();
    }

    public static String registerClient() throws IOException, ParseException {
        var accessToken = devClientAccessToken(Scope.parse("client.create"));

        var clientMetaData = new OIDCClientMetadata();
        clientMetaData.setScope(Scope.parse("read,write"));
        clientMetaData.setName("client-" + LocalDateTime.now());
        clientMetaData.setRedirectionURI(URI.create("http://client.localhost:9090/authorized"));

        return new OIDCClientRegistrationRequest(
                URI.create("http://auth.localhost:8080/connect/register"),
                clientMetaData,
                accessToken
        )
                .toHTTPRequest()
                .send()
                .getContent();
    }

    public static String getClientInfo(String clientId) throws IOException, ParseException {
        var accessToken = devClientAccessToken(Scope.parse("client.read"));

        return new ClientReadRequest(
                URI.create("http://auth.localhost:8080/connect/register?client_id=" + clientId),
                accessToken
        )
                .toHTTPRequest()
                .send()
                .getContent();
    }
}
