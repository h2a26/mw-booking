package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.Class_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepo extends JpaRepository<Class_, Long> {
    List<Class_> findAllByCountry(String country);
    default Class_ findByClassId(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Class_ not found by id: " + id));
    }
}
