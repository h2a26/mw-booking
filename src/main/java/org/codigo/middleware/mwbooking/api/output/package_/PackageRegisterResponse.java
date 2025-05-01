package org.codigo.middleware.mwbooking.api.output.package_;

import org.codigo.middleware.mwbooking.entity.Package_;

import java.math.BigDecimal;

public record PackageRegisterResponse(
        String packageName,
        int totalCredits,
        BigDecimal price,
        int expiryDays,
        String country
) {
        public static PackageRegisterResponse from(Package_ package_e) {
                return new PackageRegisterResponse(
                        package_e.getPackageName(),
                        package_e.getTotalCredits(),
                        package_e.getPrice(),
                        package_e.getExpiryDays(),
                        package_e.getCountry()
                );
        }
}

