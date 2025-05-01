package org.codigo.middleware.mwbooking.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.api.input.booking.*;
import org.codigo.middleware.mwbooking.api.input.class_.*;
import org.codigo.middleware.mwbooking.api.input.waitlist.WaitlistEntry;
import org.codigo.middleware.mwbooking.api.output.booking.*;
import org.codigo.middleware.mwbooking.commons.enum_.*;
import org.codigo.middleware.mwbooking.entity.*;
import org.codigo.middleware.mwbooking.exceptions.*;
import org.codigo.middleware.mwbooking.service.*;
import org.codigo.middleware.mwbooking.service.cache.*;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserCacheService userCacheService;
    private final BookingCacheService bookingCacheService;
    private final BookingDetailCacheService bookingDetailCacheService;
    private final BookingLockService bookingLockService;
    private final UserPackageCacheService userPackageCacheService;
    private final ClassCacheService classCacheService;
    private final WaitlistCacheService waitlistCacheService;
    private final RefundCacheService refundCacheService;

    public BookingServiceImpl(UserCacheService userCacheService, BookingCacheService bookingCacheService, BookingDetailCacheService bookingDetailCacheService, BookingLockService bookingLockService, UserPackageCacheService userPackageCacheService, ClassCacheService classCacheService, WaitlistCacheService waitlistCacheService, RefundCacheService refundCacheService) {
        this.userCacheService = userCacheService;
        this.bookingCacheService = bookingCacheService;
        this.bookingDetailCacheService = bookingDetailCacheService;
        this.bookingLockService = bookingLockService;
        this.userPackageCacheService = userPackageCacheService;
        this.classCacheService = classCacheService;
        this.waitlistCacheService = waitlistCacheService;
        this.refundCacheService = refundCacheService;
    }


    @Override
    public List<BookingConfirmedClassesResponse> bookingConfirmedClasses() {
        User user = userCacheService.getUser();
        List<Booking> bookedBookingList = bookingCacheService.findAllBookedBookingByUserId(user.getUserId());
        return bookedBookingList.stream()
                .map(BookingConfirmedClassesResponse::from)
                .toList();
    }

    @Override
    public BookingResponse bookingClass(BookingClassRequest bookingClassRequest) {

        User user = userCacheService.getUser();

        Class_ class_e = classCacheService.findById(bookingClassRequest.classId());

        validateOverlapClassTime(class_e);

        Booking booking = processBooking(user, class_e, bookingClassRequest.userPackageId());
        return BookingResponse.from(booking);
    }

    @Override
    public BookingResponse checkInBookedClass(CheckInBookedClassRequest checkInBookedClassRequest) {
        Booking bookedBooking =bookingCacheService.findById(checkInBookedClassRequest.bookingId());
        bookedBooking.setStatus(BookingStatus.CHECK_IN);
        bookingCacheService.save(bookedBooking);
        return BookingResponse.from(bookedBooking);
    }

    private void validateOverlapClassTime(Class_ class_e) {
        ZonedDateTime requestClassStartDate = class_e.getClassStartDate();
        List<Booking> bookedBookingList = bookingCacheService.findAllBookedBookingByUserId(userCacheService.getUser().getUserId());
        boolean isOverlap = bookedBookingList.stream()
                    .anyMatch(booking -> isDateWithinRange(requestClassStartDate, booking.getClassEntity().getClassStartDate(), booking.getClassEntity().getClassEndDate()));
        if (isOverlap) {
            throw new ApiBusinessException("User can’t book the overlap class time.");
        }
    }

    private boolean isDateWithinRange(ZonedDateTime requestClassStartDate, ZonedDateTime classStartDate, ZonedDateTime classEndDate) {
        // Check if the requestClassStartDate is within the range
        return (requestClassStartDate.isAfter(classStartDate) || requestClassStartDate.isEqual(classStartDate))
                && (requestClassStartDate.isBefore(classEndDate) || requestClassStartDate.isEqual(classEndDate));
    }

    private Booking processBooking(User user, Class_ class_e, long selectedUserPackageId) {
        try (BookingLockService.AutoLock lock = bookingLockService.lockForBooking(user.getUserId(), class_e.getClassId())) {
            UserPackage userPackage = userPackageCacheService.findByUserPackageId(selectedUserPackageId);
            validateSelectedPackage(user, class_e, userPackage);
            return deductCredits(user, class_e, userPackage);
        } catch (BookingConcurrencyException | InterruptedException e) {
            throw new BookingConcurrencyException("Failed to complete booking due to high demand. Please try again.", e);
        }
        // Let all other exceptions (business logic, validation, etc.) propagate
    }

    @Override
    public CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest) {

        User user = userCacheService.getUser();

        Booking booking = bookingCacheService.findById(cancelBookingRequest.bookingId());
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found by id " + cancelBookingRequest.bookingId());
        }

        if (!booking.getUser().equals(user)) {
            throw new IllegalStateException("User can't do someone else booking cancellation process.");
        }

        if (booking.isCanceled()) {
            throw new IllegalArgumentException("Booking is already canceled.");
        }

        booking.setStatus(BookingStatus.CANCELED);
        booking.setCanceled(true);
        booking.setCancellationTime(ZonedDateTime.now());

        // Update class availability
        Class_ class_e = booking.getClassEntity();
        class_e.setAvailableSlots(class_e.getAvailableSlots() + 1);
        classCacheService.save(class_e);
        bookingCacheService.save(booking);

        // Refund credits
        List<BookingDetail> bookingDetailList = bookingDetailCacheService.findAllByBookingId(booking.getBookingId());
        for (BookingDetail bookingDetail : bookingDetailList) {
            UserPackage userPackage = bookingDetail.getUserPackage();
            userPackage.setRemainingCredits(userPackage.getRemainingCredits() + bookingDetail.getCreditsDeducted());
            userPackageCacheService.save(userPackage);
            log.debug("Refunded {} credits to user package {} for booking detail {}", bookingDetail.getCreditsDeducted(), userPackage.getUserPackageId(), bookingDetail.getBookingDetailId());

            Refund refund = new Refund();
            refund.setUser(user);
            refund.setUserPackage(userPackage);
            refund.setCreditRefunded(bookingDetail.getCreditsDeducted());
            refund.setRefundTime(ZonedDateTime.now());
            refundCacheService.save(refund);
        }

        addWaitlistUserAsBookedAs(class_e);

        return CancelBookingResponse.from(booking);
    }

    private void addWaitlistUserAsBookedAs(Class_ class_e) {
        while (class_e.getAvailableSlots() > 0) {
            log.info("Class_ {} available slots: {}", class_e.getClassId(), class_e.getAvailableSlots());
            WaitlistEntry waitlistEntry = waitlistCacheService.getFromWaitlist(class_e.getClassId());
            if (waitlistEntry == null) break;
            User candidate = userCacheService.getUser(waitlistEntry.getEmail());
            //TODO: handle this method for selected package id
            processBooking(candidate, class_e, 0);
        }
    }

    private Booking deductCredits(User user, Class_ class_e, UserPackage selectedPackage) {
        boolean isWaitlist = false;

        if (class_e.getAvailableSlots() == 0) {
            isWaitlist = true;
            waitlistCacheService.addToWaitlist(user, class_e);
        } else {
            class_e.setAvailableSlots(class_e.getAvailableSlots() - 1);
        }

        int requiredCredits = class_e.getRequiredCredits();

        selectedPackage.setRemainingCredits(selectedPackage.getRemainingCredits() - requiredCredits);

        Booking booking = Booking.builder()
                .user(user)
                .classEntity(class_e)
                .bookingTime(ZonedDateTime.now())
                .status(isWaitlist ? BookingStatus.WAITLISTED : BookingStatus.BOOKED)
                .isCanceled(false)
                .build();

        classCacheService.save(class_e);
        Booking savedBooking = bookingCacheService.save(booking);

        BookingDetail detail = BookingDetail.builder()
                .booking(savedBooking)
                .userPackage(selectedPackage)
                .creditsDeducted(requiredCredits)
                .build();

        userPackageCacheService.save(selectedPackage);
        bookingDetailCacheService.save(detail);

        return booking;
    }

    private void validateSelectedPackage(User user, Class_ classEntity, UserPackage selectedPackage) {
        if (!selectedPackage.getUser().getUserId().equals(user.getUserId())) {
            throw new ApiBusinessException("Package does not belong to current user.");
        }

        if (!selectedPackage.getPackageEntity().getCountry().equals(classEntity.getCountry())) {
            throw new InvalidPackageCountryException("Package country does not match class country.");
        }

        if (selectedPackage.getStatus() != PackageStatus.ACTIVE) {
            throw new ApiBusinessException("Selected package is not active.");
        }

        if (selectedPackage.getRemainingCredits() < classEntity.getRequiredCredits()) {
            throw new InsufficientCreditsException("Not enough credits in the selected package.");
        }

        if (selectedPackage.getExpirationDate().isBefore(ZonedDateTime.now())) {
            throw new ApiBusinessException("Selected package is expired.");
        }
    }
}
