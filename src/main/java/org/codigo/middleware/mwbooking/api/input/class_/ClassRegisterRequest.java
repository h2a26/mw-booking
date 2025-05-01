package org.codigo.middleware.mwbooking.api.input.class_;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record ClassRegisterRequest(
        @NotBlank(message = "Please enter class name.")
        String className,
        @NotBlank(message = "Please enter country.")
        String country,
        int requiredCredits,
        @Min(value = 1, message = "Available Slots must be greater than 1.")
        int availableSlots,
        @NotNull(message = "Please provide the class date.")
        @FutureOrPresent(message = "Class date must be in the present or future.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") // ISO 8601 format with timezone offset
        ZonedDateTime classStartDate,
        ZonedDateTime classEndDate,
        Long businessId
) {
}

