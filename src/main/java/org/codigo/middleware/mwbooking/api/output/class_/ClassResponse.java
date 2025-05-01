package org.codigo.middleware.mwbooking.api.output.class_;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.codigo.middleware.mwbooking.entity.Class_;

import java.time.ZonedDateTime;

public record ClassResponse(
        Long id,
        String className,
        String country,
        int requiredCredits,
        int availableSlots,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm z")
        ZonedDateTime startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm z")
        ZonedDateTime endDate,
        String businessName
) {
    public static ClassResponse from(Class_ classEntity) {
        return new ClassResponse(
                classEntity.getClassId(),
                classEntity.getClassName(),
                classEntity.getCountry(),
                classEntity.getRequiredCredits(),
                classEntity.getAvailableSlots(),
                classEntity.getClassStartDate(),
                classEntity.getClassEndDate(),
                classEntity.getBusiness().getBusinessName()
        );
    }
}
