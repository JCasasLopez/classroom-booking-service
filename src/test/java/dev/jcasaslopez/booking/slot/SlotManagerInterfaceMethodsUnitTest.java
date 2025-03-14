package dev.jcasaslopez.booking.slot;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
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
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.model.WeeklySchedule;
import dev.jcasaslopez.booking.repository.BookingRepository;

@SpringBootTest
public class SlotManagerInterfaceMethodsUnitTest {
	
	@Autowired
	private SlotManagerImpl slotManagerImpl;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@BeforeEach
	// Este método se ejecuta antes de cada prueba para limpiar la base de datos.
	// Dado que las pruebas crean nuevas reservas en cada ejecución, sin esta limpieza,
	// los datos de pruebas anteriores se acumularían, afectando los resultados esperados
	// para classroomAvailableDuringPeriod_ShouldReturnExpectedResult().
	//
	// This method runs before each test to clean the database.
	// Since the tests create new bookings in each execution, without this cleanup,
	// data from previous tests would accumulate, affecting the expected results for
	// classroomAvailableDuringPeriod_ShouldReturnExpectedResult().
	void cleanDatabase() {
	    bookingRepository.deleteAll();
	}
	
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
		// Puesto que hemos ordenado la lista cronológicamente, el primer slot creado debería ser el
		// martes 4 de marzo a las 7:00 y el último el sábado 8 de marzo a las 13.00 (ver horario semanal).
		//
		// Since we have sorted the list chronologically, the first created should be on 
		// Tuesday the 4th of March at 7:00, and the last one on Friday the 8th of March at 13:00 
		// (see opening hours).
		assertAll(
			    () -> assertEquals(LocalDateTime.of(2025, 3, 4, 7, 0), weekSlots.get(0).getStart()),
			    () -> assertEquals(LocalDateTime.of(2025, 3, 8, 13, 0), weekSlots.get(weekSlots.size() - 1).getStart()),
			    () -> assertEquals(expectedNumberSlots, weekSlots.size(),
			        "The list should contain " + expectedNumberSlots + " slots for that week, but contains " + weekSlots.size())
			);
	}
		
	private static Stream<Arguments> isClassroomAvailableDuringPeriodData(){
		// Opening hours:  "CLOSED", "7:00-20:00", "CLOSED", "CLOSED", "15:30-19:30", 
		// "10:00-13:30", "CLOSED".
		// Bookings: Tuesday 12:00 - 14:00 / Friday 16:30 - 18:00.
		return Stream.of(
				// Tuesday 14:00 - Friday 16:30 -> available (in between bookings).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 14, 0), LocalDateTime.of(2025, 3, 4, 16, 30), 
						true),

				// Tuesday 11:00 - Friday 19:30 -> not available (bookings completely overlap time period).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 11, 0), LocalDateTime.of(2025, 3, 4, 19, 30), 
						false),

				// Tuesday 13:30 - Friday 16:30 -> not available (bookings partially overlap time period).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 13, 30), LocalDateTime.of(2025, 3, 4, 16, 30), 
						false),

				// Monday -> closed.
				Arguments.of(LocalDateTime.of(2025, 3, 3, 11, 0), LocalDateTime.of(2025, 3, 3, 11, 30), 
						false),

				// Tuesday 6:00 - 7:00 -> closed (just before opening).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 6, 0), LocalDateTime.of(2025, 3, 4, 7, 0), 
						false),

				// Tuesday 6:30 - 8:00 -> closed (overlapping opening time).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 6, 30), LocalDateTime.of(2025, 3, 4, 8, 0), 
						false),

				// Tuesday 7:00 - 8:00 -> open (just on opening time), no conflicting bookings.
				Arguments.of(LocalDateTime.of(2025, 3, 4, 7, 0), LocalDateTime.of(2025, 3, 4, 8, 0), 
						true),

				// Tuesday 11:00 - 11.30 -> open (within opening hours), no conflicting bookings.
				Arguments.of(LocalDateTime.of(2025, 3, 4, 11, 0), LocalDateTime.of(2025, 3, 4, 11, 30), 
						true),

				// Tuesday 19:30 - 20:30 -> closed (overlapping closing time).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 19, 30), LocalDateTime.of(2025, 3, 4, 20, 30), 
						false),

				// Tuesday 19:30 - 20:00 -> open (just before closing time), no conflicting bookings.
				Arguments.of(LocalDateTime.of(2025, 3, 4, 19, 30), LocalDateTime.of(2025, 3, 4, 20, 0), 
						true),

				// Tuesday 20:00 - 20:30 -> closed (just on closing time).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 20, 0), LocalDateTime.of(2025, 3, 4, 20, 30), 
						false),

				// Tuesday 23:00 - 23:30 -> closed (after closing time).
				Arguments.of(LocalDateTime.of(2025, 3, 4, 23, 0), LocalDateTime.of(2025, 3, 4, 23, 30), 
						false));
	}
	
	@ParameterizedTest
	@MethodSource("isClassroomAvailableDuringPeriodData")
	@DisplayName("isClassroomAvailableDuringPeriod() returns the expected result")
	// Verificamos también indirectamente el método isWithinOpeningHours(), ya que 
	// isClassroomAvailableDuringPeriod() le llama primero para comprobar que las aulas
	// están abiertas durante ese período de tiempo.
	//
	// We also indirectly verify the isWithinOpeningHours() method, as 
	// isClassroomAvailableDuringPeriod() calls it first to check that classrooms 
	// are open during that time period.
	void isClassroomAvailableDuringPeriod_ShouldReturnExpectedResult(LocalDateTime startTime, 
			LocalDateTime finishTime, boolean expectedResult) {
		// Arrange
		int idClassroom = 1;
		Booking booking1 = new Booking(0, idClassroom, 123, LocalDateTime.of(2025, 3, 4, 12, 0), 
				LocalDateTime.of(2025, 3, 4, 14, 0), null, null, BookingStatus.ACTIVE);
		Booking booking2 = new Booking(0, idClassroom, 456, LocalDateTime.of(2025, 3, 7, 16, 30), 
				LocalDateTime.of(2025, 3, 7, 18, 0), null, null, BookingStatus.ACTIVE);
		bookingRepository.save(booking1);
		bookingRepository.save(booking2);
		
		// Act
		boolean actualResult = slotManagerImpl.isClassroomAvailableDuringPeriod(idClassroom, startTime, finishTime);
		
		// Assert
		assertEquals(expectedResult, actualResult);
	}
}
