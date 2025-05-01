package org.codigo.middleware.mwbooking.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.commons.enum_.BookingStatus;
import org.codigo.middleware.mwbooking.entity.Booking;
import org.codigo.middleware.mwbooking.repository.BookingRepo;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingCacheService {

    private final BookingRepo bookingRepo;
    private final RedisUtil redisUtil;

    public BookingCacheService(BookingRepo bookingRepo, RedisUtil redisUtil) {
        this.bookingRepo = bookingRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.booking_e.key_prefix}")
    private String booking_e_key_prefix;
    @Value("${app.redis.booking_e.key_ttl}")
    private long booking_e_key_ttl;

    @Value("${app.redis.booked_booking_list_by_user_id.key_prefix}")
    private String booked_booking_list_by_user_id_key_prefix;
    @Value("${app.redis.booked_booking_list_by_user_id.key_ttl}")
    private long booked_booking_list_by_user_id_key_ttl;


    public Booking save(Booking booking) {
        Booking record = bookingRepo.save(booking);

        String key = booking_e_key_prefix + record.getBookingId();
        set(key, record);

        if (record.getStatus().equals(BookingStatus.BOOKED)) {
            updateBookedBookingListByUserId(booking);
        }

        return record;
    }

    public Booking findById(Long bookingId) {
        String key = booking_e_key_prefix + bookingId;
        Booking record = redisUtil.getHash(key, Booking.class);

        if (record == null) {
            record = bookingRepo.findByBookingId(bookingId);
            set(key, record);
        }
        return record;
    }

    public List<Booking> findAllBookedBookingByUserId(Long userId) {
        String key = booked_booking_list_by_user_id_key_prefix + userId;
        List<Booking> recordList = redisUtil.getList(key, Booking.class);

        if (recordList.isEmpty()) {
            recordList = bookingRepo.findAllByUser_UserIdAndStatus(userId, BookingStatus.BOOKED);
            setList(key, recordList);
        }
        return recordList;
    }

    private List<Booking> updateBookedBookingListByUserId(Booking booking) {
        String key = booked_booking_list_by_user_id_key_prefix + booking.getUser().getUserId();
        List<Booking> recordList = redisUtil.getList(key, Booking.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = bookingRepo.findAllByUser_UserIdAndStatus(booking.getUser().getUserId(), BookingStatus.BOOKED);
        }
        List<Booking> updatedList = recordList.stream().filter(record -> !record.getBookingId().equals(booking.getBookingId())).collect(Collectors.toList());

        updatedList.add(booking);
        setList(key, updatedList);

        return updatedList;
    }

    private void set(String key, Booking booking) {
        redisUtil.setHash(key, booking, booking_e_key_ttl, TimeUnit.MINUTES);
    }

    private void setList(String key, List<Booking> bookingList) {
        redisUtil.setList(key, bookingList, booked_booking_list_by_user_id_key_ttl, TimeUnit.MINUTES);
    }
}
