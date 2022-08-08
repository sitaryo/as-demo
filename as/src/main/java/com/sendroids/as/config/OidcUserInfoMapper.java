package com.sendroids.as.config;

import com.sendroids.as.entity.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;

import java.security.Principal;
import java.util.function.Function;

public class OidcUserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {
    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext context) {
        var user =
                (UserEntity) context.getAuthorization()
                        .<UsernamePasswordAuthenticationToken>getAttribute(Principal.class.getName())
                        .getPrincipal();
        var scopes = context.getAccessToken().getScopes();

        var claims = user.getUserProfile().toClaims(scopes);
        claims.put("sub", user.getUsername());

        return new OidcUserInfo(claims);
    }
}
