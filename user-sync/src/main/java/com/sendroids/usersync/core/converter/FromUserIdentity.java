package com.sendroids.usersync.core.converter;

import com.sendroids.usersync.core.entity.UserIdentity;

public interface FromUserIdentity<T> {
    T convert(UserIdentity source);
}
