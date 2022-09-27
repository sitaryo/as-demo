package com.sendroids.client.config;

import com.sendroids.usersynccore.config.UnionPasswordEncoderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Import(UnionPasswordEncoderConfig.class)
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests(
                        (authorize) ->
                                authorize
                                        .anyRequest()
                                        .permitAll()
                )
                .csrf()
                .disable()
                .oauth2Login()
                // 用来匹配进入 OAuth2LoginAuthenticationFilter ,默认为 /login/oauth2/code/*
                .loginProcessingUrl("/oidc/authorized")
                .and()
                .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
