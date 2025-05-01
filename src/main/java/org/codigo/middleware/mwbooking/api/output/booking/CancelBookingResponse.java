package org.codigo.middleware.mwbooking.api.output.booking;

import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.entity.Booking;

import java.time.ZonedDateTime;

public record CancelBookingResponse(
        Long bookingId,
        String className,
        ZonedDateTime bookingTime,
        BookingStatus status,
        boolean isCanceled,
        ZonedDateTime cancellationTime

) {
    public static CancelBookingResponse from(Booking booking) {
        return new CancelBookingResponse(
                booking.getBookingId(),
                booking.getClassEntity().getClassName(),
                booking.getBookingTime(),
                booking.getStatus(),
                booking.isCanceled(),
                booking.getCancellationTime()
        );
    }
}
