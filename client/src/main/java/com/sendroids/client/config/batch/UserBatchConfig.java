package com.sendroids.client.config.batch;

import com.sendroids.client.entity.UserEntity;
import com.sendroids.client.repo.UserRepo;
import com.sendroids.usersync.core.entity.Authority;
import com.sendroids.usersync.core.entity.ProfileInfo;
import com.sendroids.usersync.core.entity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
@Slf4j
public class UserBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final UserRepo userRepo;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final WebClient webClient;
    private static OAuth2AccessToken accessToken;

    public UserBatchConfig(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            UserRepo userRepo,
            ClientRegistrationRepository clientRegistrationRepository,
            WebClient webClient
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.userRepo = userRepo;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.webClient = webClient;
    }

    public OAuth2AccessToken accessToken() {
        if (
                Objects.nonNull(accessToken) &&
                        Objects.nonNull(accessToken.getExpiresAt()) &&
                        accessToken.getExpiresAt().isAfter(Instant.now())
        ) {
            return accessToken;
        }
        log.trace("get access token [use to sync user info]");
        var clientRegistration = clientRegistrationRepository.findByRegistrationId("licky-client-credentials");
        OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(
                clientRegistration);
        OAuth2AccessTokenResponse tokenResponse = new DefaultClientCredentialsTokenResponseClient().getTokenResponse(clientCredentialsGrantRequest);
        accessToken = tokenResponse.getAccessToken();

        return accessToken;
    }

    @Value("${sync.user.job-name:sync-user-job}")
    private String jobName;

    @Value("${sync.user.step-name:sync-user-step}")
    private String stepName;

    @Value("${sync.user.chunk:100}")
    private int chunk;

    @Bean
    public Job syncUserJob(
            ItemReader<UserEntity> reader,
            ItemProcessor<UserEntity, UserIdentity> processor,
            ItemWriter<UserIdentity> writer
    ) {
        var syncStep = stepBuilderFactory.get(stepName)
                .<UserEntity, UserIdentity>chunk(chunk)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
        return jobBuilderFactory.get(jobName)
                .incrementer(new RunIdIncrementer())
                .flow(syncStep)
                .end()
                .build();
    }

    @Bean
    ItemReader<UserEntity> userReader() {
        return new RepositoryItemReaderBuilder<UserEntity>()
                .repository(userRepo)
                .methodName("findByUnionIdIsNull")
                .sorts(Map.of())
                .name("users-not-sync")
                .build();
    }

    @Bean
    ItemProcessor<UserEntity, UserIdentity> syncUsersProcessor() {
        return userEntity -> {
            var userProfile = userEntity.getUserProfile();
            var data = UserIdentity.builder()
                    .accountNonExpired(userEntity.isAccountNonExpired())
                    .accountNonLocked(userEntity.isAccountNonLocked())
                    .credentialsNonExpired(userEntity.isCredentialsNonExpired())
                    .enabled(userEntity.isEnabled())
                    .username(userEntity.getUsername())
                    .password(userEntity.getPassword())
                    .authorities(
                            userEntity.getAuthorities()
                                    .stream()
                                    .map(a ->
                                            new Authority(a.getAuthority())
                                    )
                                    .collect(Collectors.toList())
                    )
                    .email(userProfile.getEmail())
                    .emailVerified(userProfile.isEmailVerified())
                    .phoneNumber(userProfile.getPhoneNumber())
                    .phoneNumberVerified(userProfile.isPhoneNumberVerified())
                    .profileInfo(
                            ProfileInfo.builder().build()
                    )
                    .build();

            return webClient.post()
                    .uri("http://rescource.localhost:8080/users/register")
                    .headers(h -> h.setBearerAuth(accessToken().getTokenValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(data)
                    .retrieve()
                    .bodyToMono(UserIdentity.class)
                    .block();
        };
    }

    @Bean
    ItemWriter<UserIdentity> updateUser() {
        return users -> {
            users.forEach(u -> log.info("username: {} unionId : {}", u.getUsername(), u.getUnionId()));

            var updateMap = users
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    UserIdentity::getUsername,
                                    u -> u
                            )
                    );

            var toUpdates = userRepo.findAllByUsernameIn(updateMap.keySet());

            toUpdates
                    .forEach(toUpdate ->
                            toUpdate.setUnionId(
                                    updateMap.get(toUpdate.getUsername()).getUnionId()
                            )
                    );

            userRepo.saveAll(toUpdates);
        };
    }

}
