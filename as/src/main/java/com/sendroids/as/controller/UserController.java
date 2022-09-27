package com.sendroids.as.controller;

import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.service.UserService;
import com.sendroids.usersyncas.ASUserController;
import com.sendroids.usersynccore.converter.FromUserIdentity;
import com.sendroids.usersynccore.converter.ToUserIdentity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController extends ASUserController<UserEntity> {

    public UserController(
            UserService userService,
            FromUserIdentity<UserEntity> fromUserIdentity,
            ToUserIdentity<UserEntity> toUserIdentity
    ) {
        super(userService, fromUserIdentity, toUserIdentity);
    }
}
