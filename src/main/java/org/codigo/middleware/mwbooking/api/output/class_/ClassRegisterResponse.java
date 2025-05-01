package org.codigo.middleware.mwbooking.api.output.class_;

import org.codigo.middleware.mwbooking.entity.Class_;

import java.time.ZonedDateTime;

public record ClassRegisterResponse(
        Long classId,
        String className,
        String country,
        int requiredCredits,
        int availableSlots,
        ZonedDateTime classDate,
        String businessName
) {
        public static ClassRegisterResponse from(Class_ classEntity) {
                return new ClassRegisterResponse(
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

