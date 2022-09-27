package com.sendroids.usersyncas;

import com.sendroids.usersyncas.exception.ASUserNotFoundException;
import com.sendroids.usersyncas.exception.ClientIdNotFoundException;
import com.sendroids.usersynccore.converter.FromUserIdentity;
import com.sendroids.usersynccore.converter.ToUserIdentity;
import com.sendroids.usersynccore.entity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
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

    private String getClientId() {
        try {
            var auth = (OAuth2IntrospectionAuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            var id = auth.getSubject();
            if (log.isDebugEnabled()) {
                log.debug("get clientId:{} in security context.", id);
            }
            return id;
        } catch (Exception e) {
            throw new ClientIdNotFoundException();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_users.register')")
    @Validated
    public UserIdentity registerUser(@Valid @RequestBody UserIdentity user) {
        var clientId = getClientId();
        return userService.findUserByClientIdAndUsername(clientId, user.getUsername())
                .map(toUserIdentity::convert)
                .orElseGet(() -> {
                    var toSave = fromUserIdentity.convert(user, clientId);
                    userService.save(toSave);
                    var saved = toUserIdentity.convert(toSave);

                    if (log.isInfoEnabled()) {
                        log.info("client {} register a user {}", clientId, saved.getUnionId());
                    }
                    return saved;
                });
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_users.update')")
    public UserIdentity updateUser(@Valid @RequestBody UserIdentity user) {
        var clientId = getClientId();
        var unionId = Objects.requireNonNull(user.getUnionId());
        return userService.findUserByClientIdAndUnionId(clientId, unionId)
                .map(dbUser -> {
                    var toUpdate = fromUserIdentity.convert(user, clientId);
                    userService.update(dbUser, toUpdate);
                    userService.save(dbUser);
                    var updated = toUserIdentity.convert(dbUser);

                    if (log.isInfoEnabled()) {
                        log.info("client {} updated a user {}", clientId, updated.getUnionId());
                    }
                    return updated;
                })
                .orElseThrow(() -> new ASUserNotFoundException(clientId, unionId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.delete')")
    public void deleteUser(@PathVariable String id) {
        var clientId = getClientId();
        var toDelete = userService.findUserByClientIdAndUnionId(clientId, id)
                .orElseThrow(() -> new ASUserNotFoundException(clientId, id));
        userService.delete(toDelete);
        if (log.isInfoEnabled()) {
            log.info("client {} delete a user {}", clientId, id);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.read')")
    public UserIdentity getUser(@PathVariable String id) {
        var clientId = getClientId();
        var dbUser = userService.findUserByClientIdAndUnionId(clientId, id)
                .orElseThrow(() -> new ASUserNotFoundException(clientId, id));
        if (log.isDebugEnabled()) {
            log.debug("client {} read user info {}", clientId, id);
        }
        return toUserIdentity.convert(dbUser);

    }

}
