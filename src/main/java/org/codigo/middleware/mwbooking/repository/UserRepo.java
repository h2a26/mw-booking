package org.codigo.middleware.mwbooking.repository;

import org.codigo.middleware.mwbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    default User findByEmail(String email) {
        return findUserByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
    }
}
