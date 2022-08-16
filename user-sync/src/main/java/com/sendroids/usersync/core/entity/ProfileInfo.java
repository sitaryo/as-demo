package com.sendroids.usersync.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileInfo {
    String address;
    String birthdate;
    String familyName;
    String gender;
    String givenName;
    String locale;
    String middleName;
    String name;
    String nickname;
    String picture;
    String preferredUsername;
    String profile;
    String updatedAt;
    String website;
    String zoneinfo;
}
