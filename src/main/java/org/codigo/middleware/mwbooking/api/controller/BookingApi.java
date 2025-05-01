package org.codigo.middleware.mwbooking.api.controller;

import org.codigo.middleware.mwbooking.api.input.booking.BookingClassRequest;
import org.codigo.middleware.mwbooking.api.input.booking.CheckInBookedClassRequest;
import org.codigo.middleware.mwbooking.api.input.class_.CancelBookingRequest;
import org.codigo.middleware.mwbooking.api.output.booking.BookingConfirmedClassesResponse;
import org.codigo.middleware.mwbooking.api.output.booking.BookingResponse;
import org.codigo.middleware.mwbooking.api.output.booking.CancelBookingResponse;
import org.codigo.middleware.mwbooking.service.BookingService;
import org.codigo.middleware.mwbooking.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingApi {

    private final BookingService bookingService;

    public BookingApi(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/confirmed-classes")
    public ResponseEntity<ApiResponse<List<BookingConfirmedClassesResponse>>> bookingConfirmedClasses() {
        List<BookingConfirmedClassesResponse> bookingConfirmed = bookingService.bookingConfirmedClasses();
        return ApiResponse.of(bookingConfirmed);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> bookingClass(@Validated @RequestBody BookingClassRequest bookingClassRequest, BindingResult result) {
        return ApiResponse.of(bookingService.bookingClass(bookingClassRequest));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<BookingResponse>> checkInBookedClass(@Validated @RequestBody CheckInBookedClassRequest checkInBookedClassRequest, BindingResult result) {
        return ApiResponse.of(bookingService.checkInBookedClass(checkInBookedClassRequest));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<CancelBookingResponse>> cancelBooking(@Validated @RequestBody CancelBookingRequest cancelBookingRequest, BindingResult result) {
        return ApiResponse.of(bookingService.cancelBooking(cancelBookingRequest));
    }
}
