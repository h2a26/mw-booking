package org.codigo.middleware.mwbooking.api.output.auth;

import org.codigo.middleware.mwbooking.entity.User;

public record VerifyOTPResponse (
        String email,
        boolean isVerified) {

    public static VerifyOTPResponse from(User user) {
        return new VerifyOTPResponse(
                user.getEmail(),
                user.isVerified());
    }
}