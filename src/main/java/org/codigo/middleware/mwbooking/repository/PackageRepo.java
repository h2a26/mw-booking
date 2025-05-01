package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.Package_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepo extends JpaRepository<Package_, Long> {
    List<Package_> findAllByCountry(String country);
    default Package_ findByPackageId(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Package_ not found by id: " + id));
    }
}
