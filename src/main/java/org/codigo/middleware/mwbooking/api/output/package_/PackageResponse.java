package org.codigo.middleware.mwbooking.api.output.package_;

import org.codigo.middleware.mwbooking.entity.Package;

import java.math.BigDecimal;

public record PackageResponse(
        Long id,
        String packageName,
        int totalCredits,
        BigDecimal price,
        int expiryDays,
        String country
) {
    public static PackageResponse from(Package packageEntity) {
        return new PackageResponse(
                packageEntity.getPackageId(),
                packageEntity.getPackageName(),
                packageEntity.getTotalCredits(),
                packageEntity.getPrice(),
                packageEntity.getExpiryDays(),
                packageEntity.getCountry()
        );
    }
}
