package org.codigo.middleware.mwbooking.config;

import org.codigo.middleware.mwbooking.exceptions.handler.SecurityExceptionResolver;
import org.codigo.middleware.mwbooking.security.token.JwtTokenFilter;
import org.codigo.middleware.mwbooking.security.token.JwtTokenGenerator;
import org.codigo.middleware.mwbooking.security.token.JwtTokenParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private static final String[] PUBLIC_ENDPOINTS = {
			"/auth/generate-otp",
			"/auth/verify-otp",
			"/auth/resend-otp",
			"/auth/register",
			"/auth/login",
			"/auth/refresh",
			"/auth/reset-password",
			"/auth/reset-password-confirm",
			"/v3/api-docs/**",
			"/swagger-ui/**",
			"/swagger-ui.html",
	};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
											JwtTokenFilter jwtTokenFilter,
											SecurityExceptionResolver securityExceptionResolver) throws Exception {
		
		http.csrf(csrf -> csrf.disable());
		http.cors(cors -> {});
		
		http.authorizeHttpRequests(req -> {
			req.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
			req.anyRequest().authenticated();
		});

		http.addFilterAfter(jwtTokenFilter, ExceptionTranslationFilter.class);
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.exceptionHandling(ex -> {
			ex.authenticationEntryPoint(securityExceptionResolver);
			ex.accessDeniedHandler(securityExceptionResolver);
		});

		return http.build();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean(initMethod = "initBean")
	JwtTokenParser jwtTokenParser() {
		return new JwtTokenParser();
	}

	@Bean
	JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	@Bean
	SecurityExceptionResolver securityExceptionResolver() {
		return new SecurityExceptionResolver();
	}

	@Bean(initMethod = "initBean")
	JwtTokenGenerator jwtTokenGenerator() {
		return new JwtTokenGenerator();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
