package com.sendroids.usersynccore.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public final class UserIdentity implements UserDetails {

    @NonNull
    String username;
    String password;
    String unionId;
    String phoneNumber;
    boolean phoneNumberVerified;
    String email;
    boolean emailVerified;
    UserIdentity host;
    Set<UserIdentity> mergedUsers;
    ProfileInfo profileInfo;

    boolean accountNonExpired;
    boolean accountNonLocked;
    boolean credentialsNonExpired;
    boolean enabled;
    Collection<Authority> authorities;
}
