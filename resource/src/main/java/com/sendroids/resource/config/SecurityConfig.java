package com.sendroids.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

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
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspectionUri("http://auth.localhost:8080/oauth2/introspect")
                                .introspectionClientCredentials("resource-client", "resource-client-password")
                        )
                );
        return http.build();
    }
}
