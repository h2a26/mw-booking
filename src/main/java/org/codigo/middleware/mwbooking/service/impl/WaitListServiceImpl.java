package org.codigo.middleware.mwbooking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.api.input.waitlist.WaitlistEntry;
import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.entity.WaitList;
import org.codigo.middleware.mwbooking.repository.WaitListRepo;
import org.codigo.middleware.mwbooking.service.WaitListService;
import org.codigo.middleware.mwbooking.service.cache.WaitListCacheService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class WaitListServiceImpl implements WaitListService {

    private final WaitListRepo waitListRepo;
    private final WaitListCacheService waitListCacheService;

    @Override
    public void addUserToWaitlist(User user, Class_ classEntity) {
        int position = waitListRepo.countByClass_(classEntity) + 1;
        WaitList waitList = WaitList.builder()
                .user(user)
                .clazz(classEntity)
                .waitlistPosition(position)
                .status("WAITLISTED")
                .build();
        waitListRepo.save(waitList);

        waitListCacheService.addToWaitlist(user, classEntity);
    }

    public Long getUserIdFromWaitlist(Class_ classEntity) {
        WaitlistEntry waitlistEntry = waitListCacheService.getFromWaitlist(classEntity.getClassId());

        if (waitlistEntry == null) {
            WaitList waitList = waitListRepo.findFirstByClazzOrderByWaitlistPositionAsc(classEntity);
            return waitList.getUser().getUserId();
        }
        return waitlistEntry.getUserId();
    }
}

