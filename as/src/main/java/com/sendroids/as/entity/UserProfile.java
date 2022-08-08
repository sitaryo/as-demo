package com.sendroids.as.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile implements Serializable, DomainModelEntity<Long, Long> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private
    Long id;

    @Access(AccessType.FIELD)
    @Version
    @Getter
    protected Long version;

    String address;
    String email;
    boolean emailVerified;
    String birthdate;
    String familyName;
    String gender;
    String givenName;
    String locale;
    String middleName;
    String name;
    String nickname;
    String picture;
    String phoneNumber;
    boolean phoneNumberVerified;
    String preferredUsername;
    String profile;
    String updatedAt;
    String website;
    String zoneinfo;

    public Map<String, Object> toClaims(Set<String> scopes) {
        var claims = new HashMap<String, Object>();

        if (!scopes.contains(OidcScopes.OPENID)) {
            return claims;
        }

        if (scopes.contains(OidcScopes.ADDRESS)) {
            claims.put("address", address);
        }
        if (scopes.contains(OidcScopes.EMAIL)) {
            claims.put("email", email);
            claims.put("emailVerified", emailVerified);
        }
        if (scopes.contains(OidcScopes.PHONE)) {
            claims.put("phoneNumber", phoneNumber);
            claims.put("phoneNumberVerified", phoneNumberVerified);
        }
        if (scopes.contains(OidcScopes.PROFILE)) {
            claims.put("birthdate", birthdate);
            claims.put("familyName", familyName);
            claims.put("gender", gender);
            claims.put("givenName", givenName);
            claims.put("locale", locale);
            claims.put("middleName", middleName);
            claims.put("name", name);
            claims.put("nickname", nickname);
            claims.put("picture", picture);
            claims.put("preferredUsername", preferredUsername);
            claims.put("profile", profile);
            claims.put("updatedAt", updatedAt);
            claims.put("website", website);
            claims.put("zoneinfo", zoneinfo);
        }

        return claims;
    }
}
