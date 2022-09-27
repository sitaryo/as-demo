package com.sendroids.as.config;

import com.sendroids.as.entity.Authority;
import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.entity.UserProfile;
import com.sendroids.usersync.core.converter.FromUserIdentity;
import com.sendroids.usersync.core.converter.ToUserIdentity;
import com.sendroids.usersync.core.entity.ProfileInfo;
import com.sendroids.usersync.core.entity.UserIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class ConvertConfig {
    @Bean
    public FromUserIdentity<UserEntity> fromUserIdentity() {
        return (userIdentity, clientId) -> {
            var user = new UserEntity();
            user.setUsername(userIdentity.getUsername());
            user.setPassword(userIdentity.getPassword());
            user.setClientId(clientId);
            user.setAuthorities(userIdentity.getAuthorities().stream().map(r -> new Authority(r.getAuthority())).collect(Collectors.toSet()));
            user.setEnabled(userIdentity.isEnabled());
            user.setAccountNonLocked(user.isAccountNonLocked());
            user.setAccountNonExpired(user.isCredentialsNonExpired());
            user.setCredentialsNonExpired(user.isCredentialsNonExpired());

            var profile = new UserProfile();

            profile.setEmail(userIdentity.getEmail());
            profile.setEmailVerified(userIdentity.isEmailVerified());
            profile.setPhoneNumber(userIdentity.getPhoneNumber());
            profile.setPhoneNumberVerified(userIdentity.isPhoneNumberVerified());

            var profileInfo = userIdentity.getProfileInfo();
            profile.setAddress(profileInfo.getAddress());
            profile.setBirthdate(profileInfo.getBirthdate());
            profile.setFamilyName(profileInfo.getFamilyName());
            profile.setGender(profileInfo.getGender());
            profile.setGivenName(profileInfo.getGivenName());
            profile.setLocale(profileInfo.getLocale());
            profile.setMiddleName(profileInfo.getMiddleName());
            profile.setName(profileInfo.getName());
            profile.setNickname(profileInfo.getNickname());
            profile.setPicture(profileInfo.getPicture());
            profile.setPreferredUsername(profileInfo.getPreferredUsername());
            profile.setProfile(profileInfo.getProfile());
            profile.setUpdatedAt(profileInfo.getUpdatedAt());
            profile.setWebsite(profileInfo.getWebsite());
            profile.setZoneinfo(profileInfo.getZoneinfo());

            user.setUserProfile(profile);

            return user;
        };
    }

    @Bean
    public ToUserIdentity<UserEntity> toUserIdentity() {
        return (user) -> {
            var profile = user.getUserProfile();
            return UserIdentity
                    .builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .unionId(user.getUnionId())
                    .enabled(user.isEnabled())
                    .accountNonLocked(user.isAccountNonLocked())
                    .accountNonExpired(user.isAccountNonExpired())
                    .credentialsNonExpired(user.isCredentialsNonExpired())
                    .email(profile.getEmail())
                    .emailVerified(profile.isEmailVerified())
                    .phoneNumber(profile.getPhoneNumber())
                    .phoneNumberVerified(profile.isPhoneNumberVerified())
                    .authorities(
                            user.getAuthorities()
                                    .stream()
                                    .map(a -> new com.sendroids.usersync.core.entity.Authority(a.getAuthority()))
                                    .collect(Collectors.toSet())
                    )
                    .profileInfo(
                            ProfileInfo.builder()
                                    .address(profile.getAddress())
                                    .birthdate(profile.getBirthdate())
                                    .familyName(profile.getFamilyName())
                                    .gender(profile.getGender())
                                    .givenName(profile.getGivenName())
                                    .locale(profile.getLocale())
                                    .middleName(profile.getMiddleName())
                                    .name(profile.getName())
                                    .nickname(profile.getNickname())
                                    .picture(profile.getPicture())
                                    .preferredUsername(profile.getPreferredUsername())
                                    .profile(profile.getProfile())
                                    .updatedAt(profile.getUpdatedAt())
                                    .website(profile.getWebsite())
                                    .zoneinfo(profile.getZoneinfo())
                                    .build()
                    ).build();
        };
    }
}
