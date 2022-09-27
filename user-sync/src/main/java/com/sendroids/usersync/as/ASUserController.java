package com.sendroids.usersync.as;

import com.sendroids.usersync.core.converter.FromUserIdentity;
import com.sendroids.usersync.core.converter.ToUserIdentity;
import com.sendroids.usersync.core.entity.UserIdentity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

public abstract class ASUserController<U> {

    private final ASUserService<U> userService;
    private final FromUserIdentity<U> fromUserIdentity;
    private final ToUserIdentity<U> toUserIdentity;

    protected ASUserController(
            ASUserService<U> userService,
            FromUserIdentity<U> fromUserIdentity,
            ToUserIdentity<U> toUserIdentity
    ) {
        this.userService = userService;
        this.fromUserIdentity = fromUserIdentity;
        this.toUserIdentity = toUserIdentity;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_users.register')")
    public UserIdentity registerUser(@RequestBody UserIdentity user) {
        var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var clientId = auth.getSubject();

        var dbUser = userService.findUserByClientIdAndUsername(clientId, user.getUsername())
                .orElseGet(() -> {
                    var toSave = fromUserIdentity.convert(user, clientId);
                    userService.save(toSave);
                    return toSave;
                });

        return toUserIdentity.convert(dbUser);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_users.update')")
    public UserIdentity updateUser(@RequestBody UserIdentity user) {
        var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var clientId = auth.getSubject();
        return userService.findUserByClientIdAndUnionId(clientId, user.getUnionId())
                .map(dbUser -> {
                    var toUpdate = fromUserIdentity.convert(user, clientId);
                    userService.update(dbUser, toUpdate);
                    userService.save(dbUser);
                    return toUserIdentity.convert(dbUser);
                })
                .orElse(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.delete')")
    public void deleteUser(@PathVariable String id) {
        var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var clientId = auth.getSubject();
        userService.findUserByClientIdAndUnionId(clientId, id)
                .ifPresent(userService::delete);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.read')")
    public UserIdentity getUser(@PathVariable String id) {
        var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var clientId = auth.getSubject();
        var dbUser = userService.findUserByClientIdAndUnionId(clientId, id)
                .orElseThrow();
        return toUserIdentity.convert(dbUser);
    }

}
