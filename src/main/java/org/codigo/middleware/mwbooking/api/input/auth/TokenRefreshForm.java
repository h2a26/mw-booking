package org.codigo.middleware.mwbooking.api.input.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshForm(
		@NotBlank(message = "Please enter login id.")
		String email,
		@NotBlank(message = "Please enter refresh token.")
		String refreshToken) {

}
