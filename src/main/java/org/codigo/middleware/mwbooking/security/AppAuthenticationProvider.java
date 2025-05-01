package org.codigo.middleware.mwbooking.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppAuthenticationProvider extends DaoAuthenticationProvider {
	
	public AppAuthenticationProvider(AppUserDetailsService appUserDetailsService, PasswordEncoder passwordEncoder) {
		super(passwordEncoder);
		setUserDetailsService(appUserDetailsService);
		setHideUserNotFoundExceptions(false);
	}

}
