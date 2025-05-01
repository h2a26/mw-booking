package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.api.input.waitlist.WaitlistEntry;
import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WaitlistCacheService {

    private final RedisUtil redisUtil;

    public WaitlistCacheService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.user_waitlist_by_class_id.key_prefix}")
    private String user_waitlist_by_class_id_key_prefix;
    @Value("${app.redis.user_waitlist_by_class_id.key_ttl}")
    private long user_waitlist_by_class_id_key_ttl;

    public void addToWaitlist(User user, Class_ class_e) {
        WaitlistEntry waitlistEntry= new WaitlistEntry(class_e.getClassId(), user.getEmail());
        String waitlistKey = user_waitlist_by_class_id_key_prefix + waitlistEntry.getClassId();
        redisUtil.pushToQueue(waitlistKey, waitlistEntry, user_waitlist_by_class_id_key_ttl, TimeUnit.MINUTES);
    }

    public WaitlistEntry getFromWaitlist(long classId) {
        String waitlistKey = user_waitlist_by_class_id_key_prefix + classId;
        //TODO: refactor to handle cache miss
        return redisUtil.popFromQueue(waitlistKey, WaitlistEntry.class);
    }
}
