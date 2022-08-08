package com.sendroids.as.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sendroids.as.entity.Authority;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = AuthorityMixinDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityMixin {
}


class AuthorityMixinDeserializer extends JsonDeserializer<Authority> {

    @Override
    public Authority deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JacksonException {
        var json = context.readTree(parser);
        return new Authority(Authority.ROLE.valueOf(json.get("authority").asText()));
    }
}
