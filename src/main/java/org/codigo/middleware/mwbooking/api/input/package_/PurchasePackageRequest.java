package org.codigo.middleware.mwbooking.api.input.package_;

import jakarta.validation.constraints.NotNull;

public record PurchasePackageRequest(
        @NotNull(message = "Please enter package id.")
        Long packageId
) {
}