package dev.jcasaslopez.booking.exception;

public class FailedAuthenticatedException extends RuntimeException {
	public FailedAuthenticatedException(String message) {
        super(message);
    }
}
