package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.UserPackage;
import org.codigo.middleware.mwbooking.repository.UserPackageRepo;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPackageCacheService {

    private final UserPackageRepo userPackageRepo;
    private final RedisUtil redisUtil;

    public UserPackageCacheService(UserPackageRepo userPackageRepo, RedisUtil redisUtil) {
        this.userPackageRepo = userPackageRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.user_package_l.key_prefix}")
    private String user_package_l_key_prefix;
    @Value("${app.redis.user_package_l.key_ttl}")
    private long user_package_l_key_ttl;

    public UserPackage save(UserPackage userPackage) {
        UserPackage record = userPackageRepo.save(userPackage);

        update_user_package_list_cache(record);

        return record;
    }

    public List<UserPackage> findUserPackagesByUserId(Long userId) {
        String key = user_package_l_key_prefix + userId;
        List<UserPackage> recordList = redisUtil.getList(key, UserPackage.class);

        if (recordList.isEmpty()) {
            recordList = userPackageRepo.findAllByUser_UserId(userId);
            setList(key, recordList);
        }
        return recordList;
    }

    public UserPackage findByUserPackageId(Long userId) {
        return userPackageRepo.findByUserPackageId(userId);
    }

    private void update_user_package_list_cache(UserPackage userPackage) {
        Long userId = userPackage.getUser().getUserId();
        String key = user_package_l_key_prefix + userId;
        List<UserPackage> recordList = redisUtil.getList(key, UserPackage.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = userPackageRepo.findAllByUser_UserId(userId);
        }
        List<UserPackage> updatedList = recordList.stream().filter(record -> !record.getUserPackageId().equals(userPackage.getUserPackageId())).collect(Collectors.toList());

        updatedList.add(userPackage);
        setList(key, updatedList);
    }

    private void setList(String key, List<UserPackage> userPackageList) {
        redisUtil.setList(key, userPackageList, user_package_l_key_ttl, TimeUnit.MINUTES);
    }
}
