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
        boolean isRefunded,
        ZonedDateTime cancellationTime

) {
    public static CancelBookingResponse from(Booking booking, boolean isRefunded) {
        return new CancelBookingResponse(
                booking.getBookingId(),
                booking.getClassEntity().getClassName(),
                booking.getBookingTime(),
                booking.getStatus(),
                booking.isCanceled(),
                isRefunded,
                booking.getCancellationTime()
        );
    }
}
