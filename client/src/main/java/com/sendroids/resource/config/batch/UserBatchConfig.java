package com.sendroids.resource.config.batch;

import com.sendroids.resource.entity.UserEntity;
import com.sendroids.resource.repo.UserRepo;
import com.sendroids.usersync.entity.Authority;
import com.sendroids.usersync.entity.ProfileInfo;
import com.sendroids.usersync.entity.UserIdentity;
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
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
@Order
@Slf4j
public class UserBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final UserRepo userRepo;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final WebClient webClient;

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

    @Value("${sync.user.job-name:sync-user-job}")
    private String jobName;

    @Value("${sync.user.step-name:sync-user-step}")
    private String stepName;

    @Value("${sync.user.chunk:100}")
    private int chunk;

    @Bean
    public Job syncUserJob(
            ItemReader<UserEntity> reader,
            ItemProcessor<UserEntity, UserIdentity<Long>> processor,
            ItemWriter<UserIdentity<Long>> writer
    ) {
        var syncStep = stepBuilderFactory.get(stepName)
                .<UserEntity, UserIdentity<Long>>chunk(chunk)
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
    @SuppressWarnings("unchecked")
    ItemProcessor<UserEntity, UserIdentity<Long>> syncUsersProcessor() {
        return userEntity -> {
            var userProfile = userEntity.getUserProfile();
            var data = UserIdentity.builder()
                    .id(userEntity.getId())
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
                            ProfileInfo
                                    .builder()
                                    .address(userProfile.getAddress())
                                    .birthdate(userProfile.getBirthdate())
                                    .familyName(userProfile.getFamilyName())
                                    .gender(userProfile.getGender())
                                    .givenName(userProfile.getGivenName())
                                    .locale(userProfile.getLocale())
                                    .middleName(userProfile.getMiddleName())
                                    .name(userProfile.getName())
                                    .nickname(userProfile.getNickname())
                                    .picture(userProfile.getPicture())
                                    .preferredUsername(userProfile.getPreferredUsername())
                                    .profile(userProfile.getProfile())
                                    .updatedAt(userProfile.getUpdatedAt())
                                    .website(userProfile.getWebsite())
                                    .zoneinfo(userProfile.getZoneinfo())
                                    .build()
                    )
                    .build();

            var clientRegistration = clientRegistrationRepository.findByRegistrationId("licky-client-credentials");

            OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest = new OAuth2ClientCredentialsGrantRequest(
                    clientRegistration);

            OAuth2AccessTokenResponse tokenResponse = new DefaultClientCredentialsTokenResponseClient().getTokenResponse(clientCredentialsGrantRequest);

            var client = new OAuth2AuthorizedClient(
                    clientRegistration,
                    "anonymous",
                    tokenResponse.getAccessToken()
            );

            return webClient.post()
                    .uri("http://rescource.localhost:7070/users/register")
                    .headers(h -> h.setBearerAuth(client.getAccessToken().getTokenValue()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(data)
                    .retrieve()
                    .bodyToMono(UserIdentity.class)
                    .block();
        };
    }

    @Bean
    ItemWriter<UserIdentity<Long>> updateUser() {
        return users -> {
            // set unionId to
            users.forEach(u -> log.info("userId: {} unionId : {}", u.getId(), u.getUnionId()));
        };
    }

}