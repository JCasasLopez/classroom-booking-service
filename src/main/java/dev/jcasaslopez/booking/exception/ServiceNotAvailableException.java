package dev.jcasaslopez.booking.exception;

public class ServiceNotAvailableException extends RuntimeException {
	public ServiceNotAvailableException(String message) {
		super(message);
	}
}