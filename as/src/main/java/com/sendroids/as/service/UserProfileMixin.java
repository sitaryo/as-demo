package com.sendroids.as.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sendroids.as.entity.UserProfile;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = UserProfileMixinDeserializer.class)
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileMixin {
}

class UserProfileMixinDeserializer extends JsonDeserializer<UserProfile> {

    @Override
    public UserProfile deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JacksonException {
        var json = context.readTree(parser);

        var userProfile = new UserProfile();
        userProfile.setId(json.get("id").asLong());
        userProfile.setVersion(json.get("version").asLong());
        userProfile.setAddress(json.get("address").asText());
        userProfile.setEmail(json.get("email").asText());
        userProfile.setEmailVerified(json.get("emailVerified").asBoolean());
        userProfile.setBirthdate(json.get("birthdate").asText());
        userProfile.setFamilyName(json.get("familyName").asText());
        userProfile.setGender(json.get("gender").asText());
        userProfile.setGivenName(json.get("givenName").asText());
        userProfile.setLocale(json.get("locale").asText());
        userProfile.setMiddleName(json.get("middleName").asText());
        userProfile.setName(json.get("name").asText());
        userProfile.setNickname(json.get("nickname").asText());
        userProfile.setPicture(json.get("picture").asText());
        userProfile.setPhoneNumber(json.get("phoneNumber").asText());
        userProfile.setPhoneNumberVerified(json.get("phoneNumberVerified").asBoolean());
        userProfile.setPreferredUsername(json.get("preferredUsername").asText());
        userProfile.setProfile(json.get("profile").asText());
        userProfile.setUpdatedAt(json.get("updatedAt").asText());
        userProfile.setWebsite(json.get("website").asText());
        userProfile.setZoneinfo(json.get("zoneinfo").asText());

        return userProfile;
    }
}
