package org.codigo.middleware.mwbooking.api.output.booking;

import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.entity.Booking;

public record BookingResponse(
        Long classId,
        String className,
        BookingStatus status
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getClassEntity().getClassId(),
                booking.getClassEntity().getClassName(),
                booking.getStatus()
        );
    }
}
