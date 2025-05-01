package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.api.input.booking.*;
import org.codigo.middleware.mwbooking.api.input.class_.*;
import org.codigo.middleware.mwbooking.api.output.booking.*;

import java.util.List;

public interface BookingService {
    List<BookingConfirmedClassesResponse> bookingConfirmedClasses();
    BookingResponse bookingClass(BookingClassRequest bookingClassRequest);
    BookingResponse checkInBookedClass(CheckInBookedClassRequest checkInBookedClassRequest);
    CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest);
}
