package org.codigo.middleware.mwbooking.service.impl;

import org.codigo.middleware.mwbooking.service.MockEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockEmailServiceImpl implements MockEmailService {
    @Override
    public boolean sendMsisdnVerificationEmail(String email, String OTP) {
        log.info("Verification email sent to {}, follow the link http://localhost:8080/cp/auth/verify-otp with request: VerifyOTPRequest(email={}, OTP={})", email, email, OTP);
        return true;
    }

    @Override
    public boolean sendResetPasswordEmail(String email, String OTP) {
        log.info("Reset Password email sent to {}, follow the link http://localhost:8080/cp/auth/reset-password-confirm with request: ResetPasswordRequest(email={}, OTP={})", email, email, OTP);
        return true;
    }

    @Override
    public boolean sendResetPinAsTempPassword(String email, String resetPin) {
        log.info("Reset Pin email sent to {}, use this rest pin {} to login and change password for your security.", email, resetPin);
        return true;
    }

    @Override
    public boolean sendRemindToCheckInWhenClassTimeStart(String email, String className) {
        log.info("Remind email sent to {}, Please check in the class {}", email, className);
        return false;
    }
}
