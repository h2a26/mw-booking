package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepo extends JpaRepository<Business, Long> {
}
