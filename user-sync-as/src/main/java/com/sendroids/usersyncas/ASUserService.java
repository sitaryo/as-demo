package com.sendroids.usersyncas;

import java.util.Optional;

public interface ASUserService<U> {
    Optional<U> findUserByClientIdAndUsername(String clientId, String username);

    Optional<U> findUserByClientIdAndUnionId(String client, String unionId);

    void update(U asUser, U clientUser);

    void delete(U user);

    void save(U user);
}
