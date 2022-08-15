package com.sendroids.usersync.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class UserIdentity<ID> implements UserDetails {

    ID id;
    String username;
    String password;
    String unionId;
    String phoneNumber;
    boolean phoneNumberVerified;
    String email;
    boolean emailVerified;
    UserIdentity<ID> host;
    Set<UserIdentity<ID>> mergedUsers;
    ProfileInfo profileInfo;

    boolean accountNonExpired;
    boolean accountNonLocked;
    boolean credentialsNonExpired;
    boolean enabled;
    Collection<Authority> authorities;
}
