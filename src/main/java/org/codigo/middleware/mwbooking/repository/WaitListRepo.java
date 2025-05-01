package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.WaitList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitListRepo extends JpaRepository<WaitList, Long> {
    @Query("SELECT COUNT(w) FROM WaitList w WHERE w.clazz = :clazz")
    int countByClass_(@Param("clazz") Class_ clazz);

    WaitList findFirstByClazzOrderByWaitlistPositionAsc(Class_ class_);
}
