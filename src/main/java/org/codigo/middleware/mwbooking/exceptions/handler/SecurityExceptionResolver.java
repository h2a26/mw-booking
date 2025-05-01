package org.codigo.middleware.mwbooking.exceptions.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class SecurityExceptionResolver implements AuthenticationEntryPoint, AccessDeniedHandler {

	@Autowired
	private HandlerExceptionResolver handlerExceptionResolver;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) {
		handlerExceptionResolver.resolveException(request, response, null, authException);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) {
		handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
	}

	
}
