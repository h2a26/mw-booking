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
    @Value("${app.redis.user_package_e.key_prefix}")
    private String user_package_e_key_prefix;
    @Value("${app.redis.user_package_e.key_ttl}")
    private long user_package_e_key_ttl;

    @Value("${app.redis.user_package_l.key_prefix}")
    private String user_package_l_key_prefix;
    @Value("${app.redis.user_package_l.key_ttl}")
    private long user_package_l_key_ttl;

    public UserPackage save(UserPackage userPackage) {
        UserPackage record = userPackageRepo.save(userPackage);

        String key = user_package_e_key_prefix + record.getUserPackageId();
        set(key, record);

        update_user_package_list(record);

        return record;
    }

    public List<UserPackage> findUserPackagesByUserIdAndCountry(Long userId, String userCountry) {
        String key = user_package_l_key_prefix + userId + ":" + userCountry;
        List<UserPackage> recordList = redisUtil.getList(key, UserPackage.class);

        if (recordList.isEmpty()) {
            recordList = userPackageRepo.findAllByUser_UserIdAndUser_Country(userId, userCountry);
            setList(key, recordList);
        }
        return recordList;
    }

    private List<UserPackage> update_user_package_list(UserPackage userPackage) {
        Long userId = userPackage.getUser().getUserId();
        String userCountry = userPackage.getUser().getCountry();
        String key = user_package_l_key_prefix + userId + ":" + userCountry;
        List<UserPackage> recordList = redisUtil.getList(key, UserPackage.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = userPackageRepo.findAllByUser_UserIdAndUser_Country(userId, userCountry);
        }
        List<UserPackage> updatedList = recordList.stream().filter(record -> !record.getUserPackageId().equals(userPackage.getUserPackageId())).collect(Collectors.toList());

        updatedList.add(userPackage);
        setList(key, updatedList);

        return updatedList;
    }

    private void set(String key, UserPackage userPackage) {
        redisUtil.setHash(key, userPackage, user_package_e_key_ttl, TimeUnit.MINUTES);
    }

    private void setList(String key, List<UserPackage> userPackageList) {
        redisUtil.setList(key, userPackageList, user_package_l_key_ttl, TimeUnit.MINUTES);
    }
}
