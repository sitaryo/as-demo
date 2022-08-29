package com.sendroids.usersync.core.converter;

import com.sendroids.usersync.core.entity.UserIdentity;

public interface ToUserIdentity<T> {
    UserIdentity convert(T source);
}
