package dev.jcasaslopez.booking.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class SlotDtoTest {
	
	private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    private SlotDto createSlotDto() {
    	return new SlotDto(0, 
    			LocalDateTime.of(2025, 3, 4, 11, 00),
    			LocalDateTime.of(2025, 3, 4, 11, 30));
    }
    
    @Test
    @DisplayName("Valid SlotDto should pass validation")
    void slotDto_WithValidData_ShouldReturnNoViolations() {
    	// Arrange
    	SlotDto slotDto = createSlotDto();
    	
    	// Act
    	Set<ConstraintViolation<SlotDto>> violations = validator.validate(slotDto);
    	
    	// Assert
    	assertTrue(violations.isEmpty(), "There should be no violations");
    }
    
    @Test
	@DisplayName("SlotDto with multiple invalid fields should fail validation")
    void slotDto_WithAllPossibleViolations_ShouldReturnMultipleViolations() {
    	// Arrange
    	// Start: 11:10 - Finish: 10:07
		SlotDto slotDto = new SlotDto(0, 
				LocalDateTime.of(2025, 4, 3, 11, 10), 
				LocalDateTime.of(2025, 4, 3, 10, 07)
		);
    	
    	// Act
    	Set<ConstraintViolation<SlotDto>> violations = validator.validate(slotDto);
    	
    	// Assert
    	assertAll(
                () -> assertEquals(3, violations.size(), "Expected exactly 3 violations"),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Finish time must be after start time"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Slot duration must be exactly 30 minutes"))),
                () -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Starting and finishing times must be valid (on the hour or half past)")))
            );
    }
    
    @Test
	@DisplayName("SlotDto with start time after finish time should fail validation")
    void slotDto_startAfterFinish_shouldFailValidation() {
    	// Arrange
    	// Start: 13:00 - Finish: 11:30
    	SlotDto slotDto = createSlotDto(); // 11:00 - 11:30
    	slotDto.setStart(slotDto.getStart().plusHours(2));
    	
    	// Act
    	Set<ConstraintViolation<SlotDto>> violations = validator.validate(slotDto);
    	
		// Assert
		assertAll(() -> assertEquals(2, violations.size(), "Expected exactly 2 violations"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Finish time must be after start time"))),
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Slot duration must be exactly 30 minutes")))
				);

	}
    
    @Test
	@DisplayName("SlotDto duration different than 30 minutes should fail validation")
    void slotDto_DifferentThan30Minutes_shouldFailValidation() {
    	// Arrange
    	// Start: 11:00 - Finish: 11:33
    	SlotDto slotDto = createSlotDto(); // 11:00 - 11:30
    	slotDto.setFinish(slotDto.getFinish().plusMinutes(3));
    	
    	// Act
    	Set<ConstraintViolation<SlotDto>> violations = validator.validate(slotDto);
    	
		// Assert
		assertAll(() -> assertEquals(2, violations.size(), "Expected exactly 2 violations"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Slot duration must be exactly 30 minutes"))),
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Starting and finishing times must be valid (on the hour or half past)")))				
			);
	}
    
    @Test
	@DisplayName("SlotDto with invalid start and finish times should fail validation")
    void slotDto_InvalidStartAndFinishTimes_shouldFailValidation() {
    	// Arrange
    	// Start: 11:03 - Finish: 11:33
    	SlotDto slotDto = createSlotDto(); // 11:00 - 11:30
    	slotDto.setStart(slotDto.getStart().plusMinutes(3));
    	slotDto.setFinish(slotDto.getFinish().plusMinutes(3));
    	
    	// Act
    	Set<ConstraintViolation<SlotDto>> violations = validator.validate(slotDto);
    	
		// Assert
		assertAll(() -> assertEquals(1, violations.size(), "Expected exactly 1 violation"), 
			() -> assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Starting and finishing times must be valid (on the hour or half past)")))
				);
	}
    
    @Test
	@DisplayName("Past slotDto should be unavailable")
    void constructorSlotDto_PastSlotDto_shouldBeUnavailable() {
    	// Arrange
    	SlotDto pastSlotDto = createSlotDto(); // 11:00 - 11:30, 4/3/2025
    	
    	// Act
    	
		// Assert
		assertFalse(pastSlotDto.isAvailable());
	}
    
    @Test
	@DisplayName("Already started slotDto should be unavailable")
    void constructorSlotDto_AlreadyStartedSlotDto_shouldBeUnavailable() {
    	// Arrange
    	LocalDateTime now = LocalDateTime.now();
    	SlotDto slotDto = new SlotDto();
    	
    	// If now it is 11:00 - 11:29, slotDto is set to 11 - 11.30
    	if(now.getMinute() < 30) {
        	slotDto.setStart(now.withMinute(0));
        	slotDto.setFinish(now.withMinute(30));
        	
        // If now it is 11:30 to 11.59, slotDto is set to 11.30 - 12
    	} else {
    		slotDto.setStart(now.withMinute(30));
        	slotDto.setFinish(now.plusHours(1).withMinute(0));
    	}
    	
    	// Act
    	
		// Assert
		assertFalse(slotDto.isAvailable());
	}
    
    @Test
	@DisplayName("Future slotDto should be available")
    void constructorSlotDto_FutureSlotDto_shouldBeAvailable() {
    	// Arrange
    	LocalDateTime start = LocalDateTime.now().withHour(11).withMinute(0).plusDays(1); 
    	LocalDateTime finish = LocalDateTime.now().withHour(11).withMinute(30).plusDays(1); 
    	SlotDto futureSlotDto = new SlotDto(0, start, finish); // 11:00 - 11:30, tomorrow
    	
    	// Act
    	
		// Assert
		assertTrue(futureSlotDto.isAvailable());
	}
    
    @Test
	@DisplayName("compareTo should return a negative value when slot starts before another")
    void compareTo_ShouldReturnNegative_WhenSlotIsBeforeAnother() {
    	// Arrange
    	SlotDto slot1 = new SlotDto (0, 
    			LocalDateTime.of(2025, 3, 4, 11, 00),
    			LocalDateTime.of(2025, 3, 4, 11, 30));
    	SlotDto slot2 = new SlotDto (0, 
    			LocalDateTime.of(2025, 3, 4, 11, 30),
    			LocalDateTime.of(2025, 3, 4, 12, 0));
    	
    	// Act
    	int resultComparison = slot1.compareTo(slot2);
    	
		// Assert
		assertTrue(resultComparison < 0);
	}
    
    @Test
   	@DisplayName("compareTo should return a positive value when slot starts after another")
       void compareTo_ShouldReturnPositive_WhenSlotIsAfterAnother() {
       	// Arrange
    	SlotDto slot1 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
    	
       	SlotDto slot2 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 00),
       			LocalDateTime.of(2025, 3, 4, 11, 30));
       	
       	// Act
       	int resultComparison = slot1.compareTo(slot2);
       	
   		// Assert
   		assertTrue(resultComparison > 0);
   	}

    @Test
   	@DisplayName("compareTo should return 0 when slots have the same start time")
       void compareTo_ShouldReturnZero_WhenSlotsHaveSameStartTime() {
       	// Arrange
    	SlotDto slot1 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
    	
       	SlotDto slot2 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
       	
       	// Act
       	int resultComparison = slot1.compareTo(slot2);
       	
   		// Assert
   		assertTrue(resultComparison == 0);
   	}
    
    @Test
   	@DisplayName("equals should return true when two slots have the same attributes")
       void equals_ShouldReturnTrue_WhenSlotsAreEqual() {
       	// Arrange
    	SlotDto slot1 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
    	
       	SlotDto slot2 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
       	
       	// Act
       	boolean resultComparison = slot1.equals(slot2);
       	
   		// Assert
   		assertTrue(resultComparison);
   	}
    
    @Test
   	@DisplayName("equals should return false when slots differ in one attribute value")
       void equals_ShouldReturnFalse_WhenSlotsDifferInOneAttributeValue() {
       	// Arrange
    	SlotDto slot1 = new SlotDto (1, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
    	
       	SlotDto slot2 = new SlotDto (0, 
       			LocalDateTime.of(2025, 3, 4, 11, 30),
       			LocalDateTime.of(2025, 3, 4, 12, 0));
       	
       	// Act
       	boolean resultComparison = slot1.equals(slot2);
       	
   		// Assert
   		assertFalse(resultComparison);
   	}

}
