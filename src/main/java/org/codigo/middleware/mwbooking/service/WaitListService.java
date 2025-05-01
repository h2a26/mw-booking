package org.codigo.middleware.mwbooking.service;

import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.User;

public interface WaitListService {
    void addUserToWaitlist(User user, Class_ classEntity);
    Long getUserIdFromWaitlist(Class_ classEntity);
}
