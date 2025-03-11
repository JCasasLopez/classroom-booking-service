package dev.jcasaslopez.booking.slot;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.model.WeeklySchedule;

@SpringBootTest
public class SlotManagerUnitTest {
	
	@Autowired
	private SlotManagerImpl slotManagerImpl;
	
	@Autowired
	private WeeklySchedule weeklySchedule;
	
	@TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        WeeklySchedule weeklySchedule() {
            return new WeeklySchedule(List.of(
                "CLOSED", "7:00-20:00", "CLOSED",
                "CLOSED", "15:30-19:30", "10:00-13:30", "CLOSED"
            ));
        }
    }
	
	private static Stream<Arguments> alignTimeToNextOpeningTimeData(){
		return Stream.of(
				// Monday 6:00 -> Tuesday 7:00
				Arguments.of(LocalDateTime.of(2025, 3, 3, 6, 0), 
						LocalDateTime.of(2025, 3, 4, 7, 0)),
				// Tuesday 23:11 -> Tuesday 7:00
				Arguments.of(LocalDateTime.of(2025, 3, 4, 23, 11), 
						LocalDateTime.of(2025, 3, 4, 7, 0)),
				// Tuesday 7:00 -> Tuesday 7:00
				Arguments.of(LocalDateTime.of(2025, 3, 4, 7, 0), 
						LocalDateTime.of(2025, 3, 4, 7, 0)),
				// Wednesday 11:00 -> Friday 15:30
				Arguments.of(LocalDateTime.of(2025, 3, 5, 11, 0), 
						LocalDateTime.of(2025, 3, 7, 15, 30)),
				// Friday 4:34 -> Friday 15:30
				Arguments.of(LocalDateTime.of(2025, 3, 7, 4, 34), 
						LocalDateTime.of(2025, 3, 7, 15, 30)));
	}
	
	private static Stream<Arguments> moveToNextDayAtOpeningTimeData(){
		return Stream.of(
				// Monday 6:00 -> Tuesday 7:00
				Arguments.of(LocalDateTime.of(2025, 3, 3, 6, 0), 
						LocalDateTime.of(2025, 3, 4, 7, 0)),
				// Tuesday 9:00 -> Friday 15:30
				Arguments.of(LocalDateTime.of(2025, 3, 4, 9, 0), 
						LocalDateTime.of(2025, 3, 7, 15, 30)),
				// Tuesday 23:11 -> Friday 15:30
				Arguments.of(LocalDateTime.of(2025, 3, 4, 23, 11), 
						LocalDateTime.of(2025, 3, 7, 15, 30)),
				// Friday 19:30 -> Saturday 10:00
				Arguments.of(LocalDateTime.of(2025, 3, 7, 19, 30), 
						LocalDateTime.of(2025, 3, 8, 10, 0)),
				// Saturday 11:30 -> Tuesday 7:00
				Arguments.of(LocalDateTime.of(2025, 3, 8, 11, 30), 
						LocalDateTime.of(2025, 3, 11, 7, 0))
				);
	}
	
	@Test
	@DisplayName("createEmptyCalendar() returns the expected list of SlotDtos")
	void createEmptyCalendar_ShouldReturnExpectedSlotDtoList() {
		// Arrange
		int idClassroom = 1;
		LocalDateTime start = LocalDateTime.of(2025, 3, 3, 6, 0);
		LocalDateTime finish = LocalDateTime.of(2025, 3, 9, 22, 0);
		int expectedNumberSlots = 41;
		
		// Act
		List<SlotDto> weekSlots = slotManagerImpl.createEmptyCalendar(idClassroom, start, finish);
		Collections.sort(weekSlots);

		// Assert
		assertAll(
			    () -> assertEquals(LocalDateTime.of(2025, 3, 4, 7, 0), weekSlots.get(0).getStart()),
			    () -> assertEquals(LocalDateTime.of(2025, 3, 8, 13, 0), weekSlots.get(weekSlots.size() - 1).getStart()),
			    () -> assertEquals(expectedNumberSlots, weekSlots.size(),
			        "The list should contain " + expectedNumberSlots + " slots for that week, but contains " + weekSlots.size())
			);
	}
	
	@ParameterizedTest
	@MethodSource("alignTimeToNextOpeningTimeData")
	@DisplayName("alignTimeToNextOpeningTime() returns the expected result")
	void alignTimeToNextOpeningTime_ShouldReturnExpectedResult(LocalDateTime time, LocalDateTime expectedResult) {
		// Arrange
		
		// Act
		LocalDateTime actualResult = slotManagerImpl.alignTimeToNextOpeningTime(time, weeklySchedule);
		
		// Assert
		assertEquals(expectedResult, actualResult, "The aligned opening time is different from the expected one");
	}
	
	@ParameterizedTest
	@MethodSource("moveToNextDayAtOpeningTimeData")
	@DisplayName("moveToNextDayAtOpeningTime() returns the expected result")
	void moveToNextDayAtOpeningTime_ShouldReturnExpectedResult(LocalDateTime time, 
			LocalDateTime expectedResult) {
		// Arrange
		
		// Act
		LocalDateTime actualResult = slotManagerImpl.moveToNextDayAtOpeningTime(time, weeklySchedule);
		
		// Assert
		assertEquals(expectedResult, actualResult, "The next opening time is different from the expected one");
	}
	
	@Test
	@DisplayName("updateSlotsAvailability() returns the expected list of SlotDtos")
	void updateSlotsAvailability_ShouldReturnExpectedSlotDtoList() {
		// Arrange
		int idClassroom = 1;
		
		// Necesitamos slots para una fecha futura, ya que si la fecha fuera pasada,
	    // los slots se marcarían como no disponibles automáticamente 
	    // (esto es debido a la implementación de SlotDto, ver su constructor).
	    //
	    // We need slots for a future date because if the date were in the past,
	    // the slots would be automatically set as unavailable 
	    // (this is due to the SlotDto implementation; see its constructor).
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime nextMondayAt6 = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
				.withHour(6).withMinute(0);
		LocalDateTime followingSundayAt22 = nextMondayAt6.plusDays(6).withHour(22).withMinute(0);
		LocalDateTime followingTuesdayAt12 = nextMondayAt6.plusDays(1).withHour(12).withMinute(0);
		LocalDateTime followingTuesdayAt14 = nextMondayAt6.plusDays(1).withHour(14).withMinute(0);
		LocalDateTime followingFridayAt1530 = nextMondayAt6.plusDays(4).withHour(15).withMinute(30);
		LocalDateTime followingFridayAt17 = nextMondayAt6.plusDays(4).withHour(17).withMinute(0);
		
		List<SlotDto> weekSlots = slotManagerImpl.createEmptyCalendar(idClassroom, 
				nextMondayAt6, followingSundayAt22);
		
		Booking booking1 = new Booking(1L, 1, 123, followingTuesdayAt12, followingTuesdayAt14, null, null, null);
		Booking booking2 = new Booking(2L, 1, 456, followingFridayAt1530, followingFridayAt17, null, null, null);
		List<Booking> bookings = List.of(booking1, booking2);
		
		// Act
		List<SlotDto> updatedSlots = slotManagerImpl.updateSlotsAvailability(weekSlots, bookings);
		
		// Assert
		assertAll(
			    () -> assertEquals(7, updatedSlots.stream().filter(slot -> !slot.isAvailable()).count(), 
			            "The number of unavailable slots is different from the expected one"),
			    // El slot que coincide con el inicio de "booking1" debería estar marcado como no disponible.
	            //
	            // The slot that matches the start of "booking1" should be marked as unavailable.
			    () -> assertTrue(updatedSlots.stream()
			            .filter(slot -> !slot.isAvailable())  
			            .filter(slot -> slot.getStart().equals(followingTuesdayAt12))  
			            .findFirst()  
			            .isPresent(),  
			            "The slot starting on " + followingTuesdayAt12 + " should be unavailable"),
			    // El slot que coincide con el final de "booking1" también debería estar marcado como no disponible.
	            //
	            // The slot that matches the end of "booking1" should also be marked as unavailable.
			    () -> assertTrue(updatedSlots.stream()
			            .filter(slot -> !slot.isAvailable())  
			            .filter(slot -> slot.getFinish().equals(followingTuesdayAt14))  
			            .findFirst()  
			            .isPresent(),  
			            "The slot starting on " + followingTuesdayAt14 + " should be unavailable"),
			    
			    // El slot inmediatamente anterior al inicio de "booking1" debería estar disponible.
	            //
	            // The slot immediately before the start of "booking1" should be available.
			    () -> assertTrue(updatedSlots.stream()
			            .filter(slot -> slot.isAvailable())  
			            .filter(slot -> slot.getStart().equals(followingTuesdayAt12.minusMinutes(30)))  
			            .findFirst()  
			            .isPresent(),  
			            "The slot starting on " + followingTuesdayAt12.minusMinutes(30) + " should be available"),
			    
			    // El slot inmediatamente posterior al final de "booking1" debería estar disponible.
	            //
	            // The slot immediately after the end of "booking1" should be available.
			    () -> assertTrue(updatedSlots.stream()
			            .filter(slot -> slot.isAvailable())  
			            .filter(slot -> slot.getStart().equals(followingTuesdayAt14.plusMinutes(30)))  
			            .findFirst()  
			            .isPresent(),  
			            "The slot starting on " + followingTuesdayAt14.plusMinutes(30) + " should be available")
			);
	}
}
