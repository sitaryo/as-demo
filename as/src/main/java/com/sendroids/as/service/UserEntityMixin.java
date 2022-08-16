package com.sendroids.as.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sendroids.as.entity.Authority;
import com.sendroids.as.entity.UserEntity;
import com.sendroids.as.entity.UserProfile;
import org.hibernate.collection.internal.PersistentSet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = UserEntityMixinDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntityMixin {
}

class UserEntityMixinDeserializer extends JsonDeserializer<UserEntity> {

    @Override
    public UserEntity deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();

        var json = context.readTree(parser);

        var user = new UserEntity();
        user.setVersion(json.get("version").asLong());
        user.setUsername(json.get("username").asText());
        user.setPassword(json.get("password").asText());
        user.setEnabled(json.get("enabled").asBoolean());
        user.setAccountNonLocked(json.get("accountNonLocked").asBoolean());
        user.setAccountNonExpired(json.get("accountNonExpired").asBoolean());
        user.setCredentialsNonExpired(json.get("credentialsNonExpired").asBoolean());

        user.setUserProfile(
                mapper.convertValue(json.findValue("userProfile"), UserProfile.class)
        );

        var authorities = new HashSet<Authority>();
        json.get("authorities").get(1)
                .forEach(a ->
                        authorities.add(mapper.convertValue(a, Authority.class))
                );
        user.setAuthorities(authorities);

        return user;
    }
}
