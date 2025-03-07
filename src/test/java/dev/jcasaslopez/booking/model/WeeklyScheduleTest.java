package dev.jcasaslopez.booking.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeeklyScheduleTest {
	
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

		WeeklySchedule weeklyOpeningTimes = new WeeklySchedule();

		// Act
		Map<DayOfWeek, OpeningHours> result = weeklyOpeningTimes.addDailyOpeningTimes(weeklyHours);

		// Assert
		assertAll(() -> assertEquals(7, result.size(), "There should be 7 days in the weekly schedule"),

				() -> assertTrue(result.get(DayOfWeek.MONDAY).isOpen(), "Monday should be open"),
				() -> assertEquals(LocalTime.of(9, 0), result.get(DayOfWeek.MONDAY).getOpeningTime()),
				() -> assertEquals(LocalTime.of(22, 0), result.get(DayOfWeek.MONDAY).getClosingTime()),

				() -> assertFalse(result.get(DayOfWeek.TUESDAY).isOpen(), "Tuesday should be closed"),

				() -> assertTrue(result.get(DayOfWeek.WEDNESDAY).isOpen(), "Wednesday should be open"),
				() -> assertEquals(LocalTime.of(10, 0), result.get(DayOfWeek.WEDNESDAY).getOpeningTime()),
				() -> assertEquals(LocalTime.of(18, 0), result.get(DayOfWeek.WEDNESDAY).getClosingTime()),

				() -> assertFalse(result.get(DayOfWeek.THURSDAY).isOpen(), "Thursday should be closed"),

				() -> assertTrue(result.get(DayOfWeek.FRIDAY).isOpen(), "Friday should be open"),
				() -> assertEquals(LocalTime.of(8, 30), result.get(DayOfWeek.FRIDAY).getOpeningTime()),
				() -> assertEquals(LocalTime.of(20, 30), result.get(DayOfWeek.FRIDAY).getClosingTime()),

				() -> assertFalse(result.get(DayOfWeek.SATURDAY).isOpen(), "Saturday should be closed"),
				() -> assertFalse(result.get(DayOfWeek.SUNDAY).isOpen(), "Sunday should be closed"));
	}
	
}
