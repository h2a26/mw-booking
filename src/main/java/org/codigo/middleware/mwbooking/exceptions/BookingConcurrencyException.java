package org.codigo.middleware.mwbooking.exceptions;

public class BookingConcurrencyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookingConcurrencyException(String message) {
		super(message);
	}

	public BookingConcurrencyException(String message, Throwable cause) {
		super(message, cause);
	}
}
