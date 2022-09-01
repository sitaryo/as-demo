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

// 一、 周期同步用户
// 1. 启用 @EnableBatchSyncUser
// 2. 提供基础 bean
//      2.1 ItemReader<USER> 从数据库读取需要同步的用户信息
//      2.2 ToUserIdentity<USER> 提供本地 user 转换为 UserIdentity 的方法
//      2.3 ItemWriter<USER> 获取同步好的 UserIdentity 更新本地 user 实体
//
// 二、 手动同步管理
// 1. 实现 SyncUserService, 并注入到容器中（需要提供 ToUserIdentity<USER> bean 作为前提）
// 2. 使用 SyncUserService 方法管理用户。包括 createUser updateUser deleteUser
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
