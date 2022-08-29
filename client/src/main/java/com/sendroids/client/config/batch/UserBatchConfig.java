package com.sendroids.client.config.batch;

import com.sendroids.client.entity.UserEntity;
import com.sendroids.client.repo.UserRepo;
import com.sendroids.usersync.client.annotation.EnableBatchSyncUser;
import com.sendroids.usersync.core.converter.ToUserIdentity;
import com.sendroids.usersync.core.entity.Authority;
import com.sendroids.usersync.core.entity.ProfileInfo;
import com.sendroids.usersync.core.entity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableBatchSyncUser
@Slf4j
public class UserBatchConfig {

    private final UserRepo userRepo;

    public UserBatchConfig(
            UserRepo userRepo
    ) {
        this.userRepo = userRepo;
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
    ToUserIdentity<UserEntity> toUserIdentity() {
        return (userEntity) -> {
            var userProfile = userEntity.getUserProfile();
            return UserIdentity.builder()
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
        };
    }

    @Bean
    ItemWriter<UserIdentity> userWriter() {
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
