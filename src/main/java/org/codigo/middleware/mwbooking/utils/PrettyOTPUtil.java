package org.codigo.middleware.mwbooking.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PrettyOTPUtil {

    private static final SecureRandom secureRandom = new SecureRandom();  // Cryptographically strong RNG
    private static final String DIGITS = "0123456789";
    private static final int OTP_LENGTH = 6;

    // Generates a "pretty" OTP (6 digits by default)
    public String generatePrettyOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);

        // Generate the first two digits
        int firstDigit = secureRandom.nextInt(DIGITS.length());
        int secondDigit = secureRandom.nextInt(DIGITS.length());

        // Ensure some pattern by repeating some digits
        otp.append(DIGITS.charAt(firstDigit))
                .append(DIGITS.charAt(firstDigit)) // Repeat the first digit
                .append(DIGITS.charAt(secondDigit))
                .append(DIGITS.charAt(secondDigit)) // Repeat the second digit
                .append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())))
                .append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));

        return otp.toString();
    }
}

