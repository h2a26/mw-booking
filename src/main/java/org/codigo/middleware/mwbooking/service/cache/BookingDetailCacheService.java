package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.BookingDetail;
import org.codigo.middleware.mwbooking.repository.BookingDetailRepo;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BookingDetailCacheService {
    private final BookingDetailRepo bookingDetailRepo;
    private final RedisUtil redisUtil;

    public BookingDetailCacheService(BookingDetailRepo bookingDetailRepo, RedisUtil redisUtil) {
        this.bookingDetailRepo = bookingDetailRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.booking_detail_l.key_prefix}")
    private String booking_detail_l_key_prefix;
    @Value("${app.redis.booking_detail_l.key_ttl}")
    private long booking_detail_l_key_ttl;

    public BookingDetail save(BookingDetail bookingDetail) {
        BookingDetail record = bookingDetailRepo.save(bookingDetail);

        updateBookingDetailListByBookingId(record);

        return record;
    }

    public BookingDetail findById(long bookingDetailId) {
        return bookingDetailRepo.findByBookingDetailId(bookingDetailId);
    }

    public List<BookingDetail> findAllByBookingId(Long bookingId) {
        String key = booking_detail_l_key_prefix + bookingId;
        List<BookingDetail> recordList = redisUtil.getList(key, BookingDetail.class);

        if (recordList.isEmpty()) {
            recordList = bookingDetailRepo.findAllByBooking_BookingId(bookingId);
            setList(key, recordList);
        }
        return recordList;
    }

    private void updateBookingDetailListByBookingId(BookingDetail bookingDetail) {
        String key = booking_detail_l_key_prefix + bookingDetail.getBooking().getBookingId();
        List<BookingDetail> recordList = redisUtil.getList(key, BookingDetail.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = bookingDetailRepo.findAllByBooking_BookingId(bookingDetail.getBooking().getBookingId());
        }
        List<BookingDetail> updatedList = recordList.stream().filter(record -> !record.getBookingDetailId().equals(bookingDetail.getBookingDetailId())).collect(Collectors.toList());

        updatedList.add(bookingDetail);
        setList(key, updatedList);

    }
    
    private void setList(String key, List<BookingDetail> bookingDetailList) {
        redisUtil.setList(key, bookingDetailList, booking_detail_l_key_ttl, TimeUnit.MINUTES);
    }
}
