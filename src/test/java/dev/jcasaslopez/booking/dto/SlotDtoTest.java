package dev.jcasaslopez.booking.dto;

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

public class SlotDtoTest {
	
	private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    private SlotDto createSlotDto() {
    	return new SlotDto(0, 
    			LocalDateTime.of(2025, 4, 3, 11, 00),
    			LocalDateTime.of(2025, 4, 3, 11, 30));
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

}
