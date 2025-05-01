package org.codigo.middleware.mwbooking.api.output.package_;


import org.codigo.middleware.mwbooking.entity.UserPackage;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PurchasePackageResponse(
        Long packageId,
        String packageName,
        int remainingCredits,
        BigDecimal price,
        String country,
        boolean isExpired,
        ZonedDateTime expirationDate
) {
    public static PurchasePackageResponse from(UserPackage userPackage) {
        return new PurchasePackageResponse(
                userPackage.getPackageEntity().getPackageId(),
                userPackage.getPackageEntity().getPackageName(),
                userPackage.getRemainingCredits(),
                userPackage.getPackageEntity().getPrice(),
                userPackage.getPackageEntity().getCountry(),
                userPackage.getExpirationDate().isBefore(ZonedDateTime.now()),
                userPackage.getExpirationDate()
        );
    }
}