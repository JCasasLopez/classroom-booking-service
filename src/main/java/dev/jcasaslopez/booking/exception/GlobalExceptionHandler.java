package dev.jcasaslopez.booking.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.jcasaslopez.booking.dto.StandardResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ClassroomNotAvailableException.class)
	public ResponseEntity<StandardResponse> handleClassroomNotAvailableException(ClassroomNotAvailableException ex){
		StandardResponse response = new StandardResponse(LocalDateTime.now(), ex.getMessage(),
				null, HttpStatus.CONFLICT);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
	
	// Esta clase se encarga de manejar las excepciones por fallo en la validación de BookingDto.
	//
	// This class handles exceptions triggered by validation failures in BookingDto.
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		// Obtenemos un mapa (Map) con los errores de validación, donde la clave es el nombre del campo
		// y el valor es el mensaje de error correspondiente.
		// 
		// We get a map (Map) with validation errors, where the key is the field name
		// and the value is the corresponding error message.
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
	            .collect(Collectors.toMap(
	                error -> error.getField(), 
	                error -> error.getDefaultMessage(),
	                (existing, replacement) -> existing 
	            ));
		logger.warn("Validation failed: {}", errors);
		
		// Convertimos el mapa de errores en un String formateado para la respuesta JSON.
		//
		// We convert the error map into a formatted String for the JSON response.
		String errorDetails = errors.entrySet().stream()
		        .map(entry -> entry.getKey() + ": " + entry.getValue())
		        .collect(Collectors.joining("; ")); 
		StandardResponse response = new StandardResponse(LocalDateTime.now(), "Validation error",
				errorDetails, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
