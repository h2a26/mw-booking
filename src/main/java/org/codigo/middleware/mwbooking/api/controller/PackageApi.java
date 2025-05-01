package org.codigo.middleware.mwbooking.api.controller;


import org.codigo.middleware.mwbooking.api.input.package_.*;
import org.codigo.middleware.mwbooking.api.output.package_.*;
import org.codigo.middleware.mwbooking.service.PackageService;
import org.codigo.middleware.mwbooking.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageApi {

    private final PackageService packageService;

    public PackageApi(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<PackageRegisterResponse>> registerPackage(@Validated @RequestBody PackageRegisterRequest packageRegisterRequest, BindingResult result) {
        PackageRegisterResponse packageRegisterResponse = packageService.registerPackage(packageRegisterRequest);
        return ApiResponse.of(packageRegisterResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAvailablePackages(@RequestParam String country) {
        List<PackageResponse> packages = packageService.getAvailablePackagesByCountry(country);
        return ApiResponse.of(packages);
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<PurchasePackageResponse>> purchasePackage(@Validated @RequestBody PurchasePackageRequest purchasePackageRequest, BindingResult result) {
        PurchasePackageResponse purchasePackageResponse = packageService.purchasePackage(purchasePackageRequest);
        return ApiResponse.of(purchasePackageResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<UserPackageResponse>>> getUserPurchasedPackages() {
        List<UserPackageResponse> userPurchasedPackages = packageService.getPurchasedPackagesByUserIdAndCountry();
        return ApiResponse.of(userPurchasedPackages);
    }
}
