package com.sendroids.resource.controller;

import com.sendroids.usersync.entity.UserIdentity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/register")
    public UserIdentity<Long> registerUser(@RequestBody UserIdentity<Long> user) {
        user.setUnionId(UUID.randomUUID().toString());
        return user;
    }

    @PutMapping("/update")
    public void updateUser(@RequestBody UserIdentity<Long> user) {

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {

    }
}
