package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.entity.Booking;
import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.entity.WaitList;

public interface WaitListService {
    void addUserToWaitlist(User user, Class_ classEntity, Booking booking);
    WaitList getUserIdFromWaitlist(Class_ classEntity);
    void removeUserFromWaitlist(Long classId, Long userId);
}
