package org.codigo.middleware.mwbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.codigo.middleware.mwbooking.entity.Package;

import java.util.List;

@Repository
public interface PackageRepo extends JpaRepository<Package, Long> {
    List<Package> findAllByCountry(String country);
    default Package findByPackageId(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Package not found by id: " + id));
    }
}
