package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepo extends JpaRepository<BookingDetail, Long> {
    List<BookingDetail> findAllByBooking_BookingId(Long bookingId);
    default BookingDetail findByBookingDetailId(long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("BookingDetail not found by id: " + id));
    }
}
