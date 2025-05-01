package org.codigo.middleware.mwbooking.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.exceptions.BookingConcurrencyException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookingLockService {

    private final RedissonClient redissonClient;

    public BookingLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public AutoLock lockForBooking(Long userId, Long classId) throws InterruptedException {
        RLock lock = redissonClient.getLock("booking_lock:" + userId + ":" + classId);
        if (lock.tryLock(10, 60, TimeUnit.SECONDS)) {
            return new AutoLock(lock);
        } else {
            throw new BookingConcurrencyException("Booking lock could not be acquired.");
        }
    }

    public static class AutoLock implements AutoCloseable {
        private final RLock lock;

        public AutoLock(RLock lock) {
            this.lock = lock;
        }

        @Override
        public void close() {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}