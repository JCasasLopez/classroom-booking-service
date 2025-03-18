package dev.jcasaslopez.booking.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.jcasaslopez.booking.dto.StandardResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ClassroomNotAvailableException.class)
	public ResponseEntity<StandardResponse> handleClassroomNotAvailableException(ClassroomNotAvailableException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.CONFLICT);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(NoSuchBookingException.class)
	public ResponseEntity<StandardResponse> handleNoSuchBookingException(NoSuchBookingException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(NoSuchClassroomException.class)
	public ResponseEntity<StandardResponse> handleNoSuchClassroomException(NoSuchClassroomException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(NoSuchSlotException.class)
	public ResponseEntity<StandardResponse> handleNoSuchSlotException(NoSuchSlotException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.CONFLICT);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	@ExceptionHandler(OutOfOpeningHoursException.class)
	public ResponseEntity<StandardResponse> handleOutOfOpeningHoursException(OutOfOpeningHoursException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler(ServiceNotAvailableException.class)
	public ResponseEntity<StandardResponse> handleServiceNotAvailableException(ServiceNotAvailableException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.SERVICE_UNAVAILABLE);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

}
