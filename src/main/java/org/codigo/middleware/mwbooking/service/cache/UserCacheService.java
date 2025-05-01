package org.codigo.middleware.mwbooking.service.cache;

import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.repository.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCacheService {

    private final UserRepo userRepo;

    public UserCacheService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findByEmailOptional(String email) {
        return userRepo.findUserByEmail(email);
    }

    public User getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return findByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found by id: " + id));
    }
}
