package com.sendroids.as.entity;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                    )
            }
    )
    @Setter(AccessLevel.NONE)
    String unionId;

    String clientId;

    @Access(AccessType.FIELD)
    @Version
    @Getter
    protected Long version;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    UserProfile userProfile;

    /**
     * 用户名
     */
    @NotNull
    String username;

    /**
     * 密码
     */
    @ToString.Exclude
    @NotNull
    String password;

    @ElementCollection(fetch = FetchType.EAGER)
    Set<Authority> authorities = new HashSet<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    boolean accountNonExpired = true;
    boolean accountNonLocked = true;
    boolean credentialsNonExpired = true;
    boolean enabled = true;

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity that = (UserEntity) o;
        return getUnionId() != null && Objects.equals(getUnionId(), that.getUnionId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
