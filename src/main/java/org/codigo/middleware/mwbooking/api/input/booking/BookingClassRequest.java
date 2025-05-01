package org.codigo.middleware.mwbooking.api.input.booking;

import jakarta.validation.constraints.NotNull;

public record BookingClassRequest(
        @NotNull(message = "Please enter class id.")
        Long classId,
        @NotNull(message = "Please enter eligible purchased package id.")
        Long userPackageId
) {
}
