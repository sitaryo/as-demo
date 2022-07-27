package com.sendroids.resource.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
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
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
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
                .mvcMatcher("/**")
                .authorizeHttpRequests()
                .mvcMatchers("/**")
                .hasAuthority("SCOPE_read")
                .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }
}