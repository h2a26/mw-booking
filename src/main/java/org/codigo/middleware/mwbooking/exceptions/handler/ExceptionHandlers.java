package org.codigo.middleware.mwbooking.exceptions.handler;

import org.codigo.middleware.mwbooking.exceptions.*;
import org.codigo.middleware.mwbooking.utils.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@Component
@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse<List<String>>> handle(ApiValidationException e) {
		return ApiResponse.of(e.getMessages(), HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiResponse<String>> handle(ApiOTPExpirationException e) {
		return ApiResponse.of(e.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiResponse<String>> handle(ApiRateLimitedException e) {
		return ApiResponse.of(e.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse<List<String>>> handle(ApiBusinessException e) {
		return ApiResponse.of(List.of(e.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
	ResponseEntity<ApiResponse<String>> handle(ApiJwtTokenExpirationException e) {
		log.error("Token Expiration Error", e);
		return ApiResponse.of("Your access token has been expired. Please refresh token again.", HttpStatus.REQUEST_TIMEOUT);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
	public ResponseEntity<ApiResponse<List<String>>> handle(ApiJwtTokenInvalidationException e) {
		log.error("Token Invalidation Error", e);
		return ApiResponse.of(List.of(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ApiResponse<List<String>>> handle(AuthenticationException e) {
		log.error("Authentication Error", e);
		List<String> messages = switch (e) {
			case BadCredentialsException ex -> List.of("Please check password.");
			case UsernameNotFoundException ex -> List.of("Please check login id.");
			case DisabledException ex -> List.of("Your account is not enabled at this time.");
			case AccountExpiredException ex -> List.of("Your account is expired.");
			default -> List.of("You need to login for this operation.");
		};
		return ApiResponse.of(messages, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiResponse<String>> handle(AccessDeniedException e) {
		log.error("Access Denied Error", e);
		return ApiResponse.of(e.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiResponse<String>> handle(Exception e) {
		log.error("System Error", e);
		return ApiResponse.of(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
