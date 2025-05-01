package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUser_UserIdAndStatus(Long userId, BookingStatus status);
    List<Booking> findAllByClassEntity_ClassIdAndStatus(Long classId, BookingStatus status);

    default Booking findByBookingId(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found by id: " + id));
    }
}
