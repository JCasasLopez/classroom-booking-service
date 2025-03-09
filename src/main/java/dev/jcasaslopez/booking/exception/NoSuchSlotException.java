package dev.jcasaslopez.booking.exception;

public class NoSuchSlotException extends RuntimeException {
	public NoSuchSlotException(String message) {
		super(message);
	}
}
