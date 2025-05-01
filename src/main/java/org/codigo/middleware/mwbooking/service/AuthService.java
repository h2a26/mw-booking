package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.api.input.auth.*;
import org.codigo.middleware.mwbooking.api.output.auth.UserProfileResponse;
import org.codigo.middleware.mwbooking.api.output.auth.UserRegistrationResponse;
import org.codigo.middleware.mwbooking.api.output.auth.VerifyOTPResponse;

public interface AuthService {
    void generateOTP(GenerateOTPRequest generateOTPRequest);
    VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest);
    void resendOTP(ResendOTPRequest resendOTPRequest);
    UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest);
    UserProfileResponse getProfile(String jwtToken);
    UserProfileResponse changePassword(String jwtToken, ChangePasswordRequest changePasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    void resetPasswordConfirm(ResetPasswordConfirmRequest resetPasswordConfirmRequest);

}
