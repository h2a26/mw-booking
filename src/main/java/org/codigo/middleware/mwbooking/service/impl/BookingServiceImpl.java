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
        return bookingCacheService.findAllBookedBookingByUserId(user.getUserId())
                .stream()
                .map(BookingConfirmedClassesResponse::from)
                .toList();
    }

    @Override
    public BookingResponse bookingClass(BookingClassRequest bookingClassRequest) {
        User user = userCacheService.getUser();
        Class_ classEntity = classCacheService.findById(bookingClassRequest.classId());
        validateNoTimeOverlap(user, classEntity);
        validatePackageForBooking(user, classEntity, bookingClassRequest.userPackageId());
        Booking booking = doBooking(user, classEntity, bookingClassRequest.userPackageId());
        return BookingResponse.from(booking);
    }

    @Override
    public BookingResponse checkInBookedClass(CheckInBookedClassRequest checkInBookedClassRequest) {
        Booking booking = bookingCacheService.findById(checkInBookedClassRequest.bookingId());
        User user = userCacheService.getUser();
        validateBookingOwnership(booking, user);
        validateCheckInWindow(booking);
        booking.setStatus(BookingStatus.CHECK_IN);
        bookingCacheService.save(booking);
        return BookingResponse.from(booking);
    }

    // Validates that the user does not have overlapping bookings for the class time
    private void validateNoTimeOverlap(User user, Class_ classEntity) {
        ZonedDateTime newClassStart = classEntity.getClassStartDate();
        ZonedDateTime newClassEnd = classEntity.getClassEndDate();
        List<Booking> userBookings = bookingCacheService.findAllBookedBookingByUserId(user.getUserId());
        boolean overlap = userBookings.stream().anyMatch(b ->
            isTimeOverlap(newClassStart, newClassEnd, b.getClassEntity().getClassStartDate(), b.getClassEntity().getClassEndDate())
        );
        if (overlap) {
            throw new ApiBusinessException("User can’t book overlapping class times.");
        }
    }

    private boolean isTimeOverlap(ZonedDateTime start1, ZonedDateTime end1, ZonedDateTime start2, ZonedDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    // Validate package ownership, country, status, credits, and expiration
    private void validatePackageForBooking(User user, Class_ classEntity, long packageId) {
        UserPackage userPackage = userPackageCacheService.findByUserPackageId(packageId);
        validateSelectedPackage(user, classEntity, userPackage);
    }

    @Override
    public CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest) {
        Booking booking = bookingCacheService.findById(cancelBookingRequest.bookingId());
        User user = userCacheService.getUser();
        validateCancelBooking(booking, user);
        doCancelBooking(booking);
        updateClassSlotsOnCancel(booking.getClassEntity());
        if (isEligibleForRefund(booking)) {
            refundBookingCredits(booking, user);
        }
        promoteWaitlistIfPossible(booking.getClassEntity());
        return CancelBookingResponse.from(booking);
    }

    private void validateCancelBooking(Booking booking, User user) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found by id");
        }
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("User can't cancel someone else's booking.");
        }
        if (booking.isCanceled()) {
            throw new IllegalArgumentException("Booking is already canceled.");
        }
        if (ZonedDateTime.now().isAfter(booking.getClassEntity().getClassStartDate())) {
            throw new IllegalStateException("Cannot cancel booking after class has started.");
        }
    }

    private void doCancelBooking(Booking booking) {
        booking.setStatus(BookingStatus.CANCELED);
        booking.setCanceled(true);
        booking.setCancellationTime(ZonedDateTime.now());
        bookingCacheService.save(booking);
    }

    private void updateClassSlotsOnCancel(Class_ classEntity) {
        classEntity.setAvailableSlots(classEntity.getAvailableSlots() + 1);
        classCacheService.save(classEntity);
    }

    private boolean isEligibleForRefund(Booking booking) {
        ZonedDateTime now = ZonedDateTime.now();
        return now.isBefore(booking.getClassEntity().getClassStartDate().minusHours(4));
    }

    // Actually perform the booking, handling waitlist if needed
    private Booking doBooking(User user, Class_ classEntity, long packageId) {
        try (BookingLockService.AutoLock lock = bookingLockService.lockForBooking(user.getUserId(), classEntity.getClassId())) {
            if (classEntity.getAvailableSlots() == 0) {
                return doAddWaitlist(user, classEntity);
            } else {
                return doNormalBooking(user, classEntity, packageId);
            }
        } catch (BookingConcurrencyException | InterruptedException e) {
            throw new BookingConcurrencyException("Failed to complete booking due to high demand. Please try again.", e);
        }
    }

    private Booking doAddWaitlist(User user, Class_ classEntity) {
        waitlistCacheService.addToWaitlist(user, classEntity);
        return Booking.builder()
                .user(user)
                .classEntity(classEntity)
                .bookingTime(ZonedDateTime.now())
                .status(BookingStatus.WAITLISTED)
                .isCanceled(false)
                .build();
    }

    private Booking doNormalBooking(User user, Class_ classEntity, long packageId) {
        UserPackage userPackage = userPackageCacheService.findByUserPackageId(packageId);
        classEntity.setAvailableSlots(classEntity.getAvailableSlots() - 1);
        userPackage.setRemainingCredits(userPackage.getRemainingCredits() - classEntity.getRequiredCredits());
        userPackageCacheService.save(userPackage);
        classCacheService.save(classEntity);
        Booking booking = Booking.builder()
                .user(user)
                .classEntity(classEntity)
                .bookingTime(ZonedDateTime.now())
                .status(BookingStatus.BOOKED)
                .isCanceled(false)
                .build();
        Booking savedBooking = bookingCacheService.save(booking);
        BookingDetail detail = BookingDetail.builder()
                .booking(savedBooking)
                .userPackage(userPackage)
                .creditsDeducted(classEntity.getRequiredCredits())
                .build();
        bookingDetailCacheService.save(detail);
        return savedBooking;
    }

    // Validate the current user owns the booking
    private void validateBookingOwnership(Booking booking, User user) {
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new ApiBusinessException("User does not own this booking.");
        }
    }

    // Validate check-in is within allowed time window
    private void validateCheckInWindow(Booking booking) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime classStart = booking.getClassEntity().getClassStartDate();
        ZonedDateTime classEnd = booking.getClassEntity().getClassEndDate();
        if (now.isBefore(classStart.minusMinutes(15)) || now.isAfter(classEnd)) {
            throw new ApiBusinessException("Check-in is only allowed 15 minutes before class until class end.");
        }
    }


    private void processBooking(User user, Class_ class_e, long selectedUserPackageId) {
        try (BookingLockService.AutoLock lock = bookingLockService.lockForBooking(user.getUserId(), class_e.getClassId())) {
            UserPackage userPackage = userPackageCacheService.findByUserPackageId(selectedUserPackageId);
            validateSelectedPackage(user, class_e, userPackage);
            deductCredits(user, class_e, userPackage);
        } catch (BookingConcurrencyException | InterruptedException e) {
            throw new BookingConcurrencyException("Failed to complete booking due to high demand. Please try again.", e);
        }
        // Let all other exceptions (business logic, validation, etc.) propagate
    }

    /**
     * Refunds credits to the user for the given booking.
     */
    private void refundBookingCredits(Booking booking, User user) {
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
    }

    /**
     * Promotes the first user on the waitlist to a booked slot if available.
     */
    private void promoteWaitlistIfPossible(Class_ class_e) {
        while (class_e.getAvailableSlots() > 0) {
            WaitlistEntry waitlistEntry = waitlistCacheService.getFromWaitlist(class_e.getClassId());
            if (waitlistEntry == null) break;
            User candidate = userCacheService.getUser(waitlistEntry.getEmail());
            try {
                // Find a valid package for the candidate
                List<UserPackage> packages = userPackageCacheService.findUserPackagesByUserId(candidate.getUserId());
                UserPackage validPackage = packages.stream()
                        .filter(p -> p.getPackageEntity().getCountry().equals(class_e.getCountry()))
                        .filter(p -> p.getStatus() == PackageStatus.ACTIVE)
                        .filter(p -> !p.getExpirationDate().isBefore(ZonedDateTime.now()))
                        .filter(p -> p.getRemainingCredits() >= class_e.getRequiredCredits())
                        .findFirst().orElse(null);
                if (validPackage == null) {
                    log.info("No valid package found for waitlist user {} for class {}", candidate.getUserId(), class_e.getClassId());
                    continue;
                }
                // Overlap check: skip if candidate has an overlapping booking
                List<Booking> userBookings = bookingCacheService.findAllBookedBookingByUserId(candidate.getUserId());
                boolean overlap = userBookings.stream().anyMatch(b ->
                    isTimeOverlap(class_e.getClassStartDate(), class_e.getClassEndDate(),
                                  b.getClassEntity().getClassStartDate(), b.getClassEntity().getClassEndDate())
                );
                if (overlap) {
                    log.info("Waitlist user {} has overlapping booking for class {}. Skipping promotion.", candidate.getUserId(), class_e.getClassId());
                    continue;
                }
                processBooking(candidate, class_e, validPackage.getUserPackageId());
                class_e.setAvailableSlots(class_e.getAvailableSlots() - 1);
                classCacheService.save(class_e);
            } catch (Exception ex) {
                log.error("Failed to promote waitlist user: {}", ex.getMessage());
            }
        }
    }


    private void deductCredits(User user, Class_ class_e, UserPackage selectedPackage) {
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
