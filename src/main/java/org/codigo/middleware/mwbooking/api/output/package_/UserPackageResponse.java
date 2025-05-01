package org.codigo.middleware.mwbooking.api.output.package_;

import org.codigo.middleware.mwbooking.commons.enum_.PackageStatus;
import org.codigo.middleware.mwbooking.entity.UserPackage;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record UserPackageResponse(
        Long packageId,
        String packageName,
        int remainingCredits,
        BigDecimal price,
        String country,
        PackageStatus status,
        boolean isExpired,
        ZonedDateTime expirationDate
) {
    public static UserPackageResponse from(UserPackage userPackage) {
        return new UserPackageResponse(
                userPackage.getPackageEntity().getPackageId(),
                userPackage.getPackageEntity().getPackageName(),
                userPackage.getRemainingCredits(),
                userPackage.getPackageEntity().getPrice(),
                userPackage.getPackageEntity().getCountry(),
                userPackage.getStatus(),
                userPackage.getExpirationDate().isBefore(ZonedDateTime.now()),
                userPackage.getExpirationDate()
        );
    }
}