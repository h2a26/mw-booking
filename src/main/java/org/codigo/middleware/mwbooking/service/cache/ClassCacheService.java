package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.Class_;
import org.codigo.middleware.mwbooking.repository.ClassRepo;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ClassCacheService {

    private final ClassRepo classRepo;
    private final RedisUtil redisUtil;

    public ClassCacheService(ClassRepo classRepo, RedisUtil redisUtil) {
        this.classRepo = classRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.class_e.key_prefix}")
    private String class_e_key_prefix;
    @Value("${app.redis.class_e.key_ttl}")
    private long class_e_key_ttl;

    @Value("${app.redis.class_l.key_prefix}")
    private String class_l_key_prefix;
    @Value("${app.redis.class_l.key_ttl}")
    private long class_l_key_ttl;

    @Value("${app.redis.classes_with_start_date.key_prefix}")
    private String classes_with_start_date_key_prefix;
    @Value("${app.redis.classes_with_start_date.key_ttl}")
    private long classes_with_start_date_key_ttl;

    @Value("${app.redis.classes_with_end_date.key_prefix}")
    private String classes_with_end_date_key_prefix;
    @Value("${app.redis.classes_with_end_date.key_ttl}")
    private long classes_with_end_date_key_ttl;

    public Class_ save(Class_ clazz) {
        Class_ record = classRepo.save(clazz);

        String key = class_e_key_prefix + record.getClassId();
        set(key, record);

        zSetAddForClassStartDate(record);
        zSetAddForClassEndDate( record);

        update_available_class_list_by_country(record);

        return record;
    }

    public Class_ findById(Long classId) {
        String key = class_e_key_prefix + classId;
        Class_ record = redisUtil.getHash(key, Class_.class);

        if (record == null) {
            record = classRepo.findByClassId(classId);
            set(key, record);
        }
        return record;
    }

    public List<Class_> findAllByCountry(String classCountry) {
        String key = class_l_key_prefix + classCountry;
        List<Class_> recordList = redisUtil.getList(key, Class_.class);

        if (recordList.isEmpty()) {
            recordList = classRepo.findAllByCountry(classCountry);
            setList(key, recordList);
        }
        recordList.forEach(this::zSetAddForClassStartDate);
        recordList.forEach(this::zSetAddForClassEndDate);
        return recordList;
    }

    private List<Class_> update_available_class_list_by_country(Class_ clazz) {
        String key = class_l_key_prefix + clazz.getCountry();
        List<Class_> recordList = redisUtil.getList(key, Class_.class);

        if (recordList.isEmpty()) {
            //will invoke db hit only once to consistence with db if redis key is deleted
            recordList = classRepo.findAllByCountry(clazz.getCountry());
        }
        List<Class_> updatedList = recordList.stream().filter(record -> !record.getClassId().equals(clazz.getClassId())).collect(Collectors.toList());

        updatedList.add(clazz);
        setList(key, updatedList);

        return updatedList;
    }

    private void set(String key, Class_ classEntity) {
        redisUtil.setHash(key, classEntity, class_e_key_ttl, TimeUnit.MINUTES);
    }

    private void setList(String key, List<Class_> classList) {
        redisUtil.setList(key, classList, class_l_key_ttl, TimeUnit.MINUTES);
    }

    public Set<Long> classesStartingAroundNow() {
        return redisUtil.getClassesAroundNow(classes_with_start_date_key_prefix,60);
    }

    public Set<Long> classesEndingAroundNow() {
        return redisUtil.getClassesAroundNow(classes_with_end_date_key_prefix,60);
    }

    private void zSetAddForClassStartDate(Class_ clazz) {
        redisUtil.saveClassDate(classes_with_start_date_key_prefix, clazz.getClassId(), clazz.getClassStartDate(), classes_with_start_date_key_ttl, TimeUnit.MINUTES);
    }

    private void zSetAddForClassEndDate(Class_ clazz) {
        redisUtil.saveClassDate(classes_with_end_date_key_prefix, clazz.getClassId(), clazz.getClassEndDate(), classes_with_end_date_key_ttl, TimeUnit.MINUTES);
    }
}
