package com.sendroids.resource.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Embeddable;
import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)

@Embeddable
public class Authority implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 1L;

    String authority;

    public Authority(ROLE role) {
        this.authority = role.name();
    }

    public enum ROLE {
        ROOT,
        USER;

        private static final String ROLE_AUTHORITY_PREFIX = "ROLE_";

        public String value() {
            return ROLE_AUTHORITY_PREFIX + this.name();
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Authority that = (Authority) o;
        return getAuthority() != null && Objects.equals(getAuthority(), that.getAuthority());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
