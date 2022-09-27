package com.sendroids.usersynccore.converter;

import com.sendroids.usersynccore.entity.UserIdentity;

public interface FromUserIdentity<T> {
    T convert(UserIdentity source,String clientId);
}
