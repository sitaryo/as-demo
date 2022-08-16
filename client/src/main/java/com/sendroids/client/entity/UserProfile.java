package com.sendroids.client.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

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

    String email;
    boolean emailVerified;
    String phoneNumber;
    boolean phoneNumberVerified;
}
