package org.codigo.middleware.mwbooking.api.input.package_;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PackageRegisterRequest(
        @NotNull(message = "Please enter package name.")
        String packageName,
        @NotNull(message = "Please enter total credits.")
        Integer totalCredits,
        BigDecimal price,
        @NotNull(message = "Please enter expiry days.")
        Integer expiryDays,
        @NotNull(message = "Please enter country.")
        String country
) {
}

