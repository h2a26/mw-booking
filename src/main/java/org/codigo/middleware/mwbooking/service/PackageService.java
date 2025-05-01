package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.api.input.package_.*;
import org.codigo.middleware.mwbooking.api.output.package_.*;

import java.util.List;

public interface PackageService {
    PackageRegisterResponse registerPackage(PackageRegisterRequest packageRegisterRequest);
    List<PackageResponse> getAvailablePackagesByCountry(String country);
    PurchasePackageResponse purchasePackage(PurchasePackageRequest purchasePackageRequest);
    List<UserPackageResponse> getPurchasedPackagesByUserIdAndCountry(Long userId, String country);
}
