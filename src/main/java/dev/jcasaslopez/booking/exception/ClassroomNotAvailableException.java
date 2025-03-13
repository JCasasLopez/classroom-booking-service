package dev.jcasaslopez.booking.exception;

public class ClassroomNotAvailableException extends RuntimeException {
	public ClassroomNotAvailableException(String message) {
		super(message);
	}
}