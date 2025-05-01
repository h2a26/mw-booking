package org.codigo.middleware.mwbooking.service.impl;

import org.codigo.middleware.mwbooking.repository.UserPackageRepo;
import org.codigo.middleware.mwbooking.service.UserPackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserPackageServiceImpl implements UserPackageService {

    private final UserPackageRepo userPackageRepo;

    public UserPackageServiceImpl(UserPackageRepo userPackageRepo) {
        this.userPackageRepo = userPackageRepo;
    }

    @Transactional
    @Override
    public int updateExpiredPackages() {
        ZonedDateTime currentDate = ZonedDateTime.now();
        return userPackageRepo.updateExpiredPackages(currentDate);
    }
}
