package org.codigo.middleware.mwbooking.api.output.booking;

import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.entity.Booking;

import java.time.ZonedDateTime;

public record BookingResponse(
        Long bookingId,
        Long classId,
        String className,
        BookingStatus status,
        ZonedDateTime classStartDate,
        ZonedDateTime classEndDate

) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getClassEntity().getClassId(),
                booking.getClassEntity().getClassName(),
                booking.getStatus(),
                booking.getClassEntity().getClassStartDate(),
                booking.getClassEntity().getClassEndDate()
        );
    }
}
