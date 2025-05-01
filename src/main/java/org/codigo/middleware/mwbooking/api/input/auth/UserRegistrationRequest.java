package org.codigo.middleware.mwbooking.api.input.auth;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(
        @NotBlank(message = "Please enter email.")
        String username,
        @NotBlank(message = "Please enter email.")
        String email,
        @NotBlank(message = "Please enter password.")
        String password,
        @NotBlank(message = "Please enter country.")
        String country
        ) {
}
