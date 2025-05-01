package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.commons.enum_.PackageStatus;
import org.codigo.middleware.mwbooking.entity.UserPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface UserPackageRepo extends JpaRepository<UserPackage, Long> {
    List<UserPackage> findAllByUser_UserId(Long userId);
    boolean existsUserPackageByUser_UserIdAndPackageEntity_PackageIdAndStatus(Long userId, Long packageId, PackageStatus status);

    @Modifying
    @Query(value = "UPDATE user_packages " +
            "SET status = 'EXPIRED' " +
            "WHERE status = 'ACTIVE' " +
            "AND expiration_date < :currentDate",
            nativeQuery = true)
    int updateExpiredPackages(ZonedDateTime currentDate);
}
