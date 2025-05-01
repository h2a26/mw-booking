package org.codigo.middleware.mwbooking.service;

public interface MockEmailService {
    boolean sendMsisdnVerificationEmail(String email, String OTP);
    boolean sendResetPasswordEmail(String email, String OTP);
    boolean sendResetPinAsTempPassword(String email, String resetPin);
    boolean sendRemindToCheckInWhenClassTimeStart(String email, String className);
}
