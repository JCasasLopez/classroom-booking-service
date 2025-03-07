package dev.jcasaslopez.booking.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeeklyOpeningTimesTest {
	
	@Autowired
	private WeeklyOpeningTimes weeklyOpeningTimes;
	
	private static Stream<Arguments> openTimes() {
		return Stream.of(
				// Lunes a la hora de apertura
				//
				// Monday at opening time
				Arguments.of(LocalDateTime.of(2025, 3, 3, 9, 0)), 
				
				// Lunes dentro del horario de apertura
				//
				// Monday during open hours
				Arguments.of(LocalDateTime.of(2025, 3, 3, 15, 30)),
				
				// Lunes justo antes del cierre
				// 
				// Monday just before closing time
				Arguments.of(LocalDateTime.of(2025, 3, 3, 21, 59)),
				
				// Martes dentro del horario de apertura
				//
				// Tuesday during open hours
				Arguments.of(LocalDateTime.of(2025, 3, 4, 10, 0)) 
		);

	}
	
	private static Stream<Arguments> closedTimes() {
		return Stream.of(
				// Lunes justo antes de la hora de apertura
				//
				// Monday right before opening time
				Arguments.of(LocalDateTime.of(2025, 3, 3, 8, 59)), 
				
				// Lunes justo a la hora de cierre
				//
				// Monday right at closing time
				Arguments.of(LocalDateTime.of(2025, 3, 3, 22, 0)),
				
				// Sábado a cualquier hora
				// 
				// Saturday anytime
				Arguments.of(LocalDateTime.of(2025, 3, 8, 15, 0)),
				
				// Domingo a cualquier hora
				// 
				// Sunday anytime
				Arguments.of(LocalDateTime.of(2025, 3, 8, 12, 0))
		);

	}
	
	// Proporciona los datos para testear getOpeningTimeForDay(). El segundo parámetro es el 
	// resultado esperado
	//
	// It provides the data to test getOpeningTimeForDay. The second parameter represents 
	// the expected result
	private static Stream<Arguments> provideTestDateTimesOpening(){
		return Stream.of(
				// Monday 
				Arguments.of(LocalDateTime.of(2025, 3, 3, 11, 0), LocalTime.of(9, 0)), 
				
				// Tuesday
				Arguments.of(LocalDateTime.of(2025, 3, 4, 11, 0), LocalTime.of(9, 0)), 
				
				// Wednesday
				Arguments.of(LocalDateTime.of(2025, 3, 5, 11, 0), LocalTime.of(9, 0)), 
				
				// Thursday
				Arguments.of(LocalDateTime.of(2025, 3, 6, 11, 0), LocalTime.of(9, 0)), 
				
				// Friday
				Arguments.of(LocalDateTime.of(2025, 3, 7, 11, 0), LocalTime.of(9, 0)), 
				
				// Saturday
				Arguments.of(LocalDateTime.of(2025, 3, 8, 11, 0), null), 
				
				// Sunday
				Arguments.of(LocalDateTime.of(2025, 3, 8, 11, 0), null)
		);
	}
	
	// Proporciona los datos para testear getOpeningTimeForDay(). El segundo parámetro es el 
		// resultado esperado
		//
		// It provides the data to test getOpeningTimeForDay. The second parameter represents 
		// the expected result
		private static Stream<Arguments> provideTestDateTimesClosing(){
			return Stream.of(
					// Monday 
					Arguments.of(LocalDateTime.of(2025, 3, 3, 11, 0), LocalTime.of(22, 0)), 
					
					// Tuesday
					Arguments.of(LocalDateTime.of(2025, 3, 4, 11, 0), LocalTime.of(22, 0)), 
					
					// Wednesday
					Arguments.of(LocalDateTime.of(2025, 3, 5, 11, 0), LocalTime.of(22, 0)), 
					
					// Thursday
					Arguments.of(LocalDateTime.of(2025, 3, 6, 11, 0), LocalTime.of(22, 0)), 
					
					// Friday
					Arguments.of(LocalDateTime.of(2025, 3, 7, 11, 0), LocalTime.of(22, 0)), 
					
					// Saturday
					Arguments.of(LocalDateTime.of(2025, 3, 8, 11, 0), null), 
					
					// Sunday
					Arguments.of(LocalDateTime.of(2025, 3, 8, 11, 0), null)
			);
		}
	
	@ParameterizedTest
	@MethodSource("openTimes")
	@DisplayName("Should return true when checking an open time")
	void isOpen_GivenOpenTime_ShouldReturnTrue(LocalDateTime givenTime){
		// Arrange
		
		// Act
		boolean result = weeklyOpeningTimes.isOpen(givenTime);
		
		// Assert
		assertTrue(result, "The classrooms should be open at that time");
		
	}
	
	@ParameterizedTest
	@MethodSource("closedTimes")
	@DisplayName("Should return false when checking a closed time")
	void isOpen_GivenClosedTime_ShouldReturnFalse(LocalDateTime givenTime){
		// Arrange
		
		// Act
		boolean result = weeklyOpeningTimes.isOpen(givenTime);
		
		// Assert
		assertFalse(result, "The classrooms should be closed at that time");
		
	}

	@Test
	@DisplayName("Should correctly parse weekly hours into DailyOpeningTimes")
	void addDayOpeningTimes_ShouldCorrectlyParseWeeklyHours() {
		// Arrange
		List<String> weeklyHours = List.of(
				"9:00-22:00", // Monday
				"CLOSED", // Tuesday
				"10:00-18:00", // Wednesday
				"CLOSED", // Thursday
				"8:30-20:30", // Friday
				"CLOSED", // Saturday
				"CLOSED" // Sunday
		);

		WeeklyOpeningTimes weeklyOpeningTimes = new WeeklyOpeningTimes();

		// Act
		List<DailyOpeningTimes> result = weeklyOpeningTimes.addDayOpeningTimes(weeklyHours);

		// Assert
		assertAll(() -> assertEquals(7, result.size(), "There should be 7 days in the weekly schedule"),

				() -> assertTrue(result.get(0).isOpen(), "Monday should be open"),
				() -> assertEquals(LocalTime.of(9, 0), result.get(0).getOpeningTime()),
				() -> assertEquals(LocalTime.of(22, 0), result.get(0).getClosingTime()),

				() -> assertFalse(result.get(1).isOpen(), "Tuesday should be closed"),

				() -> assertTrue(result.get(2).isOpen(), "Wednesday should be open"),
				() -> assertEquals(LocalTime.of(10, 0), result.get(2).getOpeningTime()),
				() -> assertEquals(LocalTime.of(18, 0), result.get(2).getClosingTime()),

				() -> assertFalse(result.get(3).isOpen(), "Thursday should be closed"),

				() -> assertTrue(result.get(4).isOpen(), "Friday should be open"),
				() -> assertEquals(LocalTime.of(8, 30), result.get(4).getOpeningTime()),
				() -> assertEquals(LocalTime.of(20, 30), result.get(4).getClosingTime()),

				() -> assertFalse(result.get(5).isOpen(), "Saturday should be closed"),
				() -> assertFalse(result.get(6).isOpen(), "Sunday should be closed"));
	}
	
	@ParameterizedTest
	@MethodSource("provideTestDateTimesOpening")
	@DisplayName("Should return opening time for that day or null if it closed")
	void getOpeningTimeForDay_GivenTime_ShouldReturnExpectedResult(LocalDateTime givenTime, 
			LocalTime expectedResult){
		// Arrange
		
		// Act
		LocalTime result = weeklyOpeningTimes.getOpeningTimeForDay(givenTime);
		
		// Assert
		assertEquals(result, expectedResult);
	}
	
	@ParameterizedTest
	@MethodSource("provideTestDateTimesClosing")
	@DisplayName("Should return closing time for that day or null if it closed")
	void getClosingTimeForDay_GivenTime_ShouldReturnExpectedResult(LocalDateTime givenTime, 
			LocalTime expectedResult){
		// Arrange
		
		// Act
		LocalTime result = weeklyOpeningTimes.getClosingTimeForDay(givenTime);
		
		// Assert
		assertEquals(result, expectedResult);
	}

}
