package org.codigo.middleware.mwbooking.exceptions;

public class ApiRateLimitedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiRateLimitedException(String message) {
		super(message);
	}
}
