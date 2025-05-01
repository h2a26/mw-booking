package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.repository.UserRepo;
import org.codigo.middleware.mwbooking.security.token.JwtTokenParser;
import org.codigo.middleware.mwbooking.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserCacheService {

    private final JwtTokenParser jwtTokenParser;
    private final UserRepo userRepo;
    private final RedisUtil redisUtil;

    public UserCacheService(JwtTokenParser jwtTokenParser, UserRepo userRepo, RedisUtil redisUtil) {
        this.jwtTokenParser = jwtTokenParser;
        this.userRepo = userRepo;
        this.redisUtil = redisUtil;
    }

    @Value("${app.redis.user_e.key_prefix}")
    private String user_e_key_prefix;
    @Value("${app.redis.user_e.key_ttl}")
    private long user_e_key_ttl;

    public User save(User user) {
        User record = userRepo.save(user);

        String key = user_e_key_prefix + record.getEmail();
        set(key, record);

        return record;
    }

    public User findByEmail(String email) {
        String key = user_e_key_prefix + email;
        User record = redisUtil.getHash(key, User.class);

        if (record == null) {
            record = userRepo.findByEmail(email);
            set(key, record);
        }
        return record;
    }

    public Optional<User> findByEmailOrElse(String email) {
        String key = user_e_key_prefix + email;
        User record = redisUtil.getHash(key, User.class);

        if (record == null) {
            Optional<User> optionalUser = userRepo.findUserByEmail(email);
            optionalUser.ifPresent(user -> set(key, user));
            return optionalUser;
        }
        return Optional.ofNullable(record);
    }

    public User getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return findByEmail(email);
    }
    public User getUser(String email) {
        return findByEmail(email);
    }

    private void set(String key, User user) {
        redisUtil.setHash(key, user, user_e_key_ttl, TimeUnit.MINUTES);
    }
}
