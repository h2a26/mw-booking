package org.codigo.middleware.mwbooking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.entity.Booking;
import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.entity.WaitList;
import org.codigo.middleware.mwbooking.repository.WaitListRepo;
import org.codigo.middleware.mwbooking.service.WaitListService;
import org.codigo.middleware.mwbooking.service.cache.WaitListCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WaitListServiceImpl implements WaitListService {

    private final WaitListRepo waitListRepo;
    private final WaitListCacheService waitListCacheService;

    @Override
    public void addUserToWaitlist(User user, Class_ classEntity, Booking booking) {
        int position = waitListRepo.countByClass_(classEntity) + 1;
        WaitList waitList = WaitList.builder()
                .user(user)
                .clazz(classEntity)
                .booking(booking)
                .waitlistPosition(position)
                .build();
        WaitList saved = waitListRepo.save(waitList);
        waitListCacheService.addToWaitlist(saved);
    }

    public WaitList getUserIdFromWaitlist(Class_ classEntity) {
        WaitList waitListFromCache = waitListCacheService.getFromWaitlist(classEntity.getClassId());

        if (waitListFromCache == null) {
            return waitListRepo.findFirstByClazzOrderByWaitlistPositionAsc(classEntity);
        }
        return waitListFromCache;
    }

    @Override
    @Transactional
    public void removeUserFromWaitlist(Long classId, Long userId) {
        waitListRepo.deleteByClassIdAndUserId(classId, userId);
    }
}

