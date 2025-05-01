package org.codigo.middleware.mwbooking.api.output.auth;

import org.codigo.middleware.mwbooking.entity.User;

public record UserRegistrationResponse (
     String username,
     String email,
     String country,
     boolean isVerified) {

    public static UserRegistrationResponse from(User user) {
        return new UserRegistrationResponse(
                user.getUsername(),
                user.getEmail(),
                user.getCountry(),
                user.isVerified());
    }
}
