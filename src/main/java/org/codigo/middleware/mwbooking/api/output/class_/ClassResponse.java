package org.codigo.middleware.mwbooking.api.output.class_;

import org.codigo.middleware.mwbooking.entity.Class_;

import java.time.ZonedDateTime;

public record ClassResponse(
        Long id,
        String className,
        String country,
        int requiredCredits,
        int availableSlots,
        ZonedDateTime classDate,
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
                classEntity.getBusiness().getBusinessName()
        );
    }
}
