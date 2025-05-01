package org.codigo.middleware.mwbooking.security;


import org.codigo.middleware.mwbooking.entity.Role;
import org.codigo.middleware.mwbooking.entity.User;
import org.codigo.middleware.mwbooking.service.cache.UserCacheService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Service
public class AppUserDetailsService implements UserDetailsService{

	private final UserCacheService userCacheService;

    public AppUserDetailsService(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }

    @Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userCacheService.findByEmail(email);

		return org.springframework.security.core.userdetails.User
				.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(getAuthorities(user))
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.disabled(false)
				.build();
	}


	public Collection<GrantedAuthority> getAuthorities(User user) {
		Set<GrantedAuthority> authorities = new HashSet<>();

		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoles().stream()
				.map(Role::getName)
				.findFirst()
				.orElseThrow(() ->new UsernameNotFoundException("User has no roles."))));

		user.getRoles().forEach(role ->
			role.getPermissions().forEach(permission ->
				authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
			)
		);

		return authorities;
	}

}
