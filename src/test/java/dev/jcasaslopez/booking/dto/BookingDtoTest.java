package dev.jcasaslopez.booking.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.jcasaslopez.booking.enums.BookingStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

// Estos tests no verifican todos los posibles escenarios. En particular, no prueban  
// las validaciones estándar como @NotNull, @Future o @FutureOrPresent, ya que asumimos  
// que Hibernate Validator las maneja correctamente. En su lugar, nos enfocamos en probar  
// los escenarios generales (objeto válido/todos los datos inválidos) y las validaciones  
// personalizadas en métodos booleanos, donde podría haber errores en la lógica de negocio.
//
// These tests do not cover all possible scenarios. Specifically, they do not test  
// standard validations like @NotNull, @Future, or @FutureOrPresent, as we assume  
// that Hibernate Validator handles them correctly. Instead, we focus on testing  
// general scenarios (valid object/all data invalid) and custom boolean validations,  
// where business logic errors are more likely.
public class BookingDtoTest {
	
	private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    private BookingDto createBookingDto() {
    	LocalDateTime now = LocalDateTime.now();
    	return new BookingDto(0, 10, 10,
    			now.plusHours(1).withMinute(30),
    			now.plusHours(2).withMinute(30),
    			now,
    			"Test booking",
    			BookingStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Valid BookingDto should pass validation")
    void bookingDto_WithValidData_ShouldReturnNoViolations() {
    	// Arrange
    	BookingDto bookingDto = createBookingDto();
    	
    	// Act
    	Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    	
    	// Assert
    	assertTrue(violations.isEmpty(), "There should be no violations");
    }
    
	@Test
	@DisplayName("Booking with multiple invalid fields should fail validation")
    void bookingDto_WithAllPossibleViolations_ShouldReturnMultipleViolations() {
    	// Arrange
		LocalDateTime now = LocalDateTime.now();
		BookingDto bookingDto = new BookingDto(0, 
				null, 
				null, 
				now.minusDays(1), 
				now.minusDays(2),
				now, 
				"Booking test", 
				BookingStatus.ACTIVE 
		);
    	
    	// Act
    	Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    	
    	// Assert
    	assertAll(
                () -> assertEquals(7, violations.size(), "Expected exactly 7 violations"),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("idClassroom field is required"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("idUser field is required"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Start time must be in the present or future"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Finish time must be in the future"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Finish time must be after start time"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Booking cannot be shorter than 30 minutes or longer than 2 hours"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Starting and finishing times must be valid (on the hour or half past)")))
            );
    }
	
	@Test
	@DisplayName("BookingDto with start time after finish time should fail validation")
    void bookingDto_startAfterFinish_shouldFailValidation() {
    	// Arrange
		BookingDto bookingDto = createBookingDto();
		bookingDto.setStart(bookingDto.getStart().plusHours(2));
    	
    	// Act
    	Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    	
		// Assert
		assertAll(() -> assertEquals(2, violations.size(), "Expected exactly 2 violations"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Finish time must be after start time"))),
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Booking cannot be shorter than 30 minutes or longer than 2 hours")))
				);

	}
	
	@Test
	@DisplayName("Booking longer than 2 hours should fail validation")
    void bookingDto_durationExceedsLimit_shouldFailValidation() {
    	// Arrange
		BookingDto bookingDto = createBookingDto();
		bookingDto.setFinish(bookingDto.getFinish().plusHours(5));
    	
    	// Act
    	Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    	
		// Assert
		assertAll(() -> assertEquals(1, violations.size(), "Expected exactly 1 violation"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Booking cannot be shorter than 30 minutes or longer than 2 hours")))
				);

	}
	
	@Test
	@DisplayName("Booking with invalid start or finish time should fail validation")
    void bookingDto_invalidStartOrFinishTime_shouldFailValidation() {
    	// Arrange
		BookingDto bookingDto = createBookingDto();
		bookingDto.setStart(bookingDto.getStart().withMinute(17));
		bookingDto.setFinish(bookingDto.getFinish().withMinute(17));

    	// Act
    	Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    	
		// Assert
		assertAll(() -> assertEquals(1, violations.size(), "Expected exactly 1 violation"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage()
					.equals("Starting and finishing times must be valid (on the hour or half past)")))
				);

	}
	
	@Test
	@DisplayName("BookingDto constructor should initialize fields correctly")
	void bookingDto_Constructor_ShouldInitializeFieldsCorrectly() {
	    // Arrange
	    LocalDateTime now = LocalDateTime.now();
	    LocalDateTime start = now.plusHours(1).withMinute(30);
	    LocalDateTime finish = now.plusHours(2).withMinute(30);
	    
	    // Act
	    BookingDto booking = new BookingDto(1, 10, 20, now.plusHours(1).withMinute(30)
	    		, now.plusHours(2).withMinute(30), now, "Test comment", BookingStatus.CANCELLED);
	    
	    // Assert
	    assertAll(
	        () -> assertEquals(1, booking.getIdBooking()),
	        () -> assertEquals(10, booking.getIdClassroom()),
	        () -> assertEquals(20, booking.getIdUser()),
	        () -> assertEquals(start, booking.getStart()),
	        () -> assertEquals(finish, booking.getFinish()),
	        () -> assertEquals(now, booking.getTimestamp()),
	        () -> assertEquals("Test comment", booking.getComment()),
	        () -> assertEquals(BookingStatus.CANCELLED, booking.getStatus())
	    );
	}
	
	@Test
	@DisplayName("BookingDto should set default values for timestamp and status if null")
	void bookingDto_Constructor_ShouldSetDefaultValuesIfNull() {
	    // Arrange
	    LocalDateTime start = LocalDateTime.now().plusHours(1).withMinute(30);
	    LocalDateTime finish = LocalDateTime.now().plusHours(2).withMinute(30);
	    
	    // Act
	    BookingDto booking = new BookingDto(1, 10, 20, start, finish, null, "Test comment", null);
	    
	    // Assert
	    assertAll(
	        () -> assertNotNull(booking.getTimestamp(), "Timestamp should not be null"),
	        () -> assertEquals(BookingStatus.ACTIVE, booking.getStatus(), "Status should default to ACTIVE if null")
	    );
	}


}
