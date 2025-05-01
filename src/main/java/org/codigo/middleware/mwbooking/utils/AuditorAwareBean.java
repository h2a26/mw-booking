package org.codigo.middleware.mwbooking.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareBean implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))  // Exclude anonymous user token
                .map(auth -> auth.getName())
                .or(() -> Optional.of("System")); // Fallback to "System" if unauthenticated
    }

}
