package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.Package_;
import org.codigo.middleware.mwbooking.repository.PackageRepo;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PackageCacheService {

    private final PackageRepo packageRepo;
    private final RedisUtil redisUtil;

    public PackageCacheService(PackageRepo packageRepo, RedisUtil redisUtil) {
        this.packageRepo = packageRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.package_e.key_prefix}")
    private String package_e_key_prefix;
    @Value("${app.redis.package_e.key_ttl}")
    private long package_e_key_ttl;

    @Value("${app.redis.package_l.key_prefix}")
    private String package_l_key_prefix;
    @Value("${app.redis.package_l.key_ttl}")
    private long package_l_key_ttl;

    public Package_ save(Package_ package_e) {
        Package_ record = packageRepo.save(package_e);

        update_available_package_list_by_country(record);

        return record;
    }

    public Package_ findById(long packageId) {
        return packageRepo.findByPackageId(packageId);
    }

    public List<Package_> findAllByCountry(String country) {
        String key = package_l_key_prefix + country;
        List<Package_> recordList = redisUtil.getList(key, Package_.class);

        if (recordList.isEmpty()) {
            recordList = packageRepo.findAllByCountry(country);
            setList(key, recordList);
        }
        return recordList;
    }

    private void update_available_package_list_by_country(Package_ package_e) {
        String key = package_l_key_prefix + package_e.getCountry();
        List<Package_> recordList = redisUtil.getList(key, Package_.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = packageRepo.findAllByCountry(package_e.getCountry());
        }
        List<Package_> updatedList = recordList.stream().filter(record -> !record.getPackageId().equals(package_e.getPackageId())).collect(Collectors.toList());

        updatedList.add(package_e);
        setList(key, updatedList);

    }

    private void set(String key, Package_ package_e) {
        redisUtil.setHash(key, package_e, package_e_key_ttl, TimeUnit.MINUTES);
    }

    private void setList(String key, List<Package_> packageList) {
        redisUtil.setList(key, packageList, package_l_key_ttl, TimeUnit.MINUTES);
    }
}
