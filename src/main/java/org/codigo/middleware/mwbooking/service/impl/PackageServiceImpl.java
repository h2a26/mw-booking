package org.codigo.middleware.mwbooking.service.impl;

import org.codigo.middleware.mwbooking.api.input.package_.*;
import org.codigo.middleware.mwbooking.api.output.package_.*;
import org.codigo.middleware.mwbooking.commons.enum_.PackageStatus;
import org.codigo.middleware.mwbooking.entity.Package_;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.entity.UserPackage;
import org.codigo.middleware.mwbooking.repository.UserPackageRepo;
import org.codigo.middleware.mwbooking.service.PackageService;
import org.codigo.middleware.mwbooking.service.cache.PackageCacheService;
import org.codigo.middleware.mwbooking.service.cache.UserCacheService;
import org.codigo.middleware.mwbooking.service.cache.UserPackageCacheService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService {

    private final MockPaymentService mockPaymentService;
    private final PackageCacheService packageCacheService;
    private final UserPackageCacheService userPackageCacheService;
    private final UserCacheService userCacheService;

    public PackageServiceImpl(MockPaymentService mockPaymentService, PackageCacheService packageCacheService, UserPackageCacheService userPackageCacheService, UserCacheService userCacheService) {
        this.mockPaymentService = mockPaymentService;
        this.packageCacheService = packageCacheService;
        this.userPackageCacheService = userPackageCacheService;
        this.userCacheService = userCacheService;
    }

    @Override
    public PackageRegisterResponse registerPackage(PackageRegisterRequest packageRegisterRequest) {
        Package_ package_e = Package_.builder()
                            .packageName(packageRegisterRequest.packageName())
                            .totalCredits(packageRegisterRequest.totalCredits())
                            .price(packageRegisterRequest.price())
                            .expiryDays(packageRegisterRequest.expiryDays())
                            .country(packageRegisterRequest.country())
                            .build();
        Package_ savedPackage = packageCacheService.save(package_e);
        return PackageRegisterResponse.from(savedPackage);
    }

    @Override
    public List<PackageResponse> getAvailablePackagesByCountry(String country) {
        List<Package_> aPackages = packageCacheService.findAllByCountry(country);
        return aPackages.stream()
                .map(PackageResponse::from)
                .toList();
    }

    @Override
    public PurchasePackageResponse purchasePackage(PurchasePackageRequest purchasePackageRequest) {
        User user = userCacheService.getUser();

        Package_ selectedPackage = packageCacheService.findById(purchasePackageRequest.packageId());

        mockPaymentService.paymentCharge(selectedPackage, user);

        UserPackage userPackage = new UserPackage();
        userPackage.setUser(user);
        userPackage.setPackageEntity(selectedPackage);
        userPackage.setRemainingCredits(selectedPackage.getTotalCredits());
        userPackage.setStatus(PackageStatus.ACTIVE);
        userPackage.setExpirationDate(ZonedDateTime.now().plusDays(selectedPackage.getExpiryDays()));

        userPackageCacheService.save(userPackage);
        return PurchasePackageResponse.from(userPackage);
    }


    @Override
    public List<UserPackageResponse> getPurchasedPackagesByUserIdAndCountry(Long userId) {
        List<UserPackage> userPackageList = userPackageCacheService.findUserPackagesByUserIdAndCountry(userId);
        return userPackageList.stream()
                .map(UserPackageResponse::from)
                .collect(Collectors.toList());
    }

}
