package org.codigo.middleware.mwbooking.api.output.auth;

import org.codigo.middleware.mwbooking.entity.User;

public record UserProfileResponse (
        String username,
        String email,
        String password,
        String country,
        boolean isVerified) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getCountry(),
                user.isVerified());
    }
}
