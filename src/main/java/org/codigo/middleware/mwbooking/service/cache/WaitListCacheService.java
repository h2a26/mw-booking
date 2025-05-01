package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.WaitList;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WaitListCacheService {

    private final RedisUtil redisUtil;

    public WaitListCacheService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.user_waitlist_by_class_id.key_prefix}")
    private String user_waitlist_by_class_id_key_prefix;
    @Value("${app.redis.user_waitlist_by_class_id.key_ttl}")
    private long user_waitlist_by_class_id_key_ttl;

    public void addToWaitlist(WaitList waitList) {
        String waitlistKey = user_waitlist_by_class_id_key_prefix + waitList.getClazz().getClassId();
        redisUtil.pushToQueue(waitlistKey, waitList, user_waitlist_by_class_id_key_ttl, TimeUnit.MINUTES);
    }

    public WaitList getFromWaitlist(long classId) {
        String waitlistKey = user_waitlist_by_class_id_key_prefix + classId;
        return redisUtil.popFromQueue(waitlistKey, WaitList.class);
    }
}
