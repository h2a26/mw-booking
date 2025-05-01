package org.codigo.middleware.mwbooking.service;

public interface OTPService {
    String generateOTP();
    void verifyOTP(String email, String otpToVerify);
    void storeOTP(String email, String otp);
    String getOTP(String email);
    void deleteOTP(String email);
    void rateLimitingOTP(String email);
}
