package org.codigo.middleware.mwbooking.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void setWithExpiration(String key, Object value, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public void setWithoutExpiration(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public void expire(String key, long duration, TimeUnit timeUnit) {
        redisTemplate.expire(key, duration, timeUnit);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public <T> void setList(String key, List<T> list, long duration, TimeUnit timeUnit) {
        if (list.isEmpty()) {
            redisTemplate.delete(key); // Clear key if list is empty
            return;
        }

        String serializedList = serialize(list);
        redisTemplate.opsForValue().set(key, serializedList);
        expire(key, duration, timeUnit);
    }


    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = (String) redisTemplate.opsForValue().get(key);

        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        return deserialize(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public <T> void setHash(String key, T object, long duration, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().putAll(key, objectMapper.convertValue(object, Map.class));
            expire(key, duration, timeUnit);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store hash in Redis", e);
        }
    }

    public <T> T getHash(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return objectMapper.convertValue(entries, clazz);
    }


    // FIFO Logic: Add item to the queue (LPUSH)
    public <T> void pushToQueue(String key, T item, long duration, TimeUnit timeUnit) {
        String serializedItem = serialize(item);
        redisTemplate.opsForList().leftPush(key, serializedItem); // Add to the left (queue front)
        expire(key, duration, timeUnit);
    }

    // FIFO Logic: Retrieve and remove the item from the queue (RPOP)
    public <T> T popFromQueue(String key, Class<T> clazz) {
        String json = (String) redisTemplate.opsForList().rightPop(key); // Remove from the right (queue rear)

        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveClassDate(String key, Long classId, ZonedDateTime classDate, long duration, TimeUnit timeUnit) {
        ZSetOperations<String, Long> zSetOps = getZSetOperationsForLong();
        long score = classDate.toEpochSecond();
        zSetOps.add(key, classId, score);
        expire(key, duration, timeUnit);
    }

    public Set<Long> getClassesAroundNow(String key, int bufferSeconds) {
        long currentEpochSecond = Instant.now().getEpochSecond();
        return getClassesBetween(key,currentEpochSecond - bufferSeconds, currentEpochSecond + bufferSeconds);
    }

    private Set<Long> getClassesBetween(String key, long start, long end) {
        ZSetOperations<String, Long> zSetOps = getZSetOperationsForLong();
        return zSetOps.rangeByScore(key, start, end);
    }

    @SuppressWarnings("unchecked")
    private ZSetOperations<String, Long> getZSetOperationsForLong() {
        return (ZSetOperations<String, Long>) (ZSetOperations<?, ?>) redisTemplate.opsForZSet();
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize object", e);
        }
    }

    private <T> T deserialize(String json, CollectionType collectionType) {
        try {
            return objectMapper.readValue(json, collectionType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not deserialize object", e);
        }
    }
}
