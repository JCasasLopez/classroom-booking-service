package dev.jcasaslopez.booking.exception;

public class OutOfOpeningHoursException extends RuntimeException {
	public OutOfOpeningHoursException(String message) {
		super(message);
	}
}
