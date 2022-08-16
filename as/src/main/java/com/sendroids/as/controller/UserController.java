package com.sendroids.as.controller;

import com.sendroids.as.entity.Authority;
import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.entity.UserProfile;
import com.sendroids.as.service.UserService;
import com.sendroids.usersync.entity.ProfileInfo;
import com.sendroids.usersync.entity.UserIdentity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UserEntity UserIdentity2UserEntity(UserIdentity<Long> userIdentity) {
        var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auth.getSubject();
        var user = new UserEntity();
        user.setUsername(userIdentity.getUsername());
        user.setPassword(userIdentity.getPassword());
        user.setClientId(auth.getSubject());
        user.setUnionId(UUID.randomUUID().toString());
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
    }

    private UserIdentity<Long> userEntity2UserIdentity(UserEntity user) {
        var profile = user.getUserProfile();
        return UserIdentity
                .<Long>builder()
                .id(user.getId())
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
                                .map(a -> new com.sendroids.usersync.entity.Authority(a.getAuthority()))
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
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('SCOPE_users.register')")
    public UserIdentity<Long> registerUser(@RequestBody UserIdentity<Long> user) {
        var toSave = UserIdentity2UserEntity(user);
        userService.save(toSave);
        return userEntity2UserIdentity(toSave);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_users.update')")
    public void updateUser(@RequestBody UserIdentity<Long> user) {

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.delete')")
    public void deleteUser(@PathVariable String id) {

    }
}