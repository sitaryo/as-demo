package com.sendroids.usersynccore.converter;

import com.sendroids.usersynccore.entity.UserIdentity;

public interface ToUserIdentity<T> {
    UserIdentity convert(T source);
}
