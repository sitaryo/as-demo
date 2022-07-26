package com.sendroids.as.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .cors()
                .configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
                    corsConfig.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
                    corsConfig.setAllowedOriginPatterns(Collections.singletonList(CorsConfiguration.ALL));
                    corsConfig.addExposedHeader("Authorization");
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                })
                .and()
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public ProviderSettings providerSettings() {
//        default endpoint
//      .authorizationEndpoint("/oauth2/authorize")
//		.tokenEndpoint("/oauth2/token")
//		.jwkSetEndpoint("/oauth2/jwks")
//		.tokenRevocationEndpoint("/oauth2/revoke")
//		.tokenIntrospectionEndpoint("/oauth2/introspect")
//		.oidcClientRegistrationEndpoint("/connect/register")
//		.oidcUserInfoEndpoint("/userinfo");
        return ProviderSettings.builder()
                .issuer("http://auth.localhost:8080")
                .build();
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(){
        return new InMemoryRegisteredClientRepository(
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-client")
                        .clientSecret("{noop}licky-password")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://client.localhost:9090/login/oauth2/code/licky-client-oidc")
                        .redirectUri("http://client.localhost:9090/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                .build(),
                RegisteredClient
                        .withId(UUID.randomUUID().toString())
                        .clientId("licky-public")
//                        .clientSecret("{noop}licky--password")
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        // fixme change redirectUri
//                        .redirectUri("http://client.localhost:9090/login/oauth2/code/licky-client-oidc")
                        .redirectUri("http://127.0.0.1:8989/authorized")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .scope("write")
                        .build()
        );

    }

}
