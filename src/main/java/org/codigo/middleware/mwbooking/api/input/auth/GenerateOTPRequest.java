package org.codigo.middleware.mwbooking.api.input.auth;

import jakarta.validation.constraints.NotBlank;

public record GenerateOTPRequest(
        @NotBlank(message = "Please enter email.")
        String email
        ) {
}
