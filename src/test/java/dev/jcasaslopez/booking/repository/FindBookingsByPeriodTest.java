package dev.jcasaslopez.booking.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;
import jakarta.persistence.EntityManager;

@DataJpaTest
public class FindBookingsByPeriodTest {
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	// Datos de prueba para findBookingsForClassroomByPeriod_BookingsWithinPeriodTest.
	// (Aula, número esperado de reservas activas, inicio del período, fin del período).
	//
	// Test data for findBookingsForClassroomByPeriod_BookingsWithinPeriodTest.
	// (Classroom, expected result, start, finish).
	private static Stream<Arguments> queriesByPeriod() {
		
		// Reservas configuradas en setupTestBookings():
		//
		// Bookings set in setupTestBookings():
		// ┌────────────┬───────────────┬──────────┐
		// │ Classroom  │  Hours        │ Status   │
		// ├────────────┼───────────────┼──────────┤
		// │ 1          │ 14:00-15:30   │ ACTIVE   │
		// │ 1          │ 17:00-18:00   │ ACTIVE   │
		// │ 1          │ 19:00-20:30   │ CANCELLED│
		// │ 4          │ 17:30-18:30   │ ACTIVE   │
		// └────────────┴───────────────┴──────────┘

		// TODAS las búsquedas incluyen una reserva CANCELADA dentro del período de búsqueda.
		// Estas reservas no deben ser devueltas, lo que verifica que el método excluye 
		// correctamente las reservas canceladas.
		//
		// ALL searches include one CANCELLED booking within the search period.
		// These bookings should NOT be returned, ensuring that the method 
		// correctly excludes cancelled reservations.
		return Stream.of(
		    // 13:00 - 22:00 → No hay edge cases, todas las reservas activas entran completamente en el período.
		    //
			// 13:00 - 22:00 → No edge cases, all active bookings fully fit within the period.
		    Arguments.of(1, 2, LocalDateTime.of(2025, 3, 2, 13, 0), LocalDateTime.of(2025, 3, 2, 22, 00)),

		    // 14:00 - 20:30 → Edge case: La búsqueda comienza y termina exactamente en los bordes de dos reservas activas.
		    //
		    // 14:00 - 20:30 → Edge case: The search starts and ends exactly at the edges of two active bookings.
		    Arguments.of(1, 2, LocalDateTime.of(2025, 3, 2, 14, 0), LocalDateTime.of(2025, 3, 2, 20, 30)),

		    // 14:30 - 17:00 → El período de búsqueda cae **entre dos reservas activas**, sin tocar sus bordes.
		    // No debería devolver ninguna, ya que no hay ninguna que comience o termine en este rango.
		    //
		    // 14:30 - 17:00 → The search period falls **between two active bookings**, without touching their edges.
		    // Should return none, as no booking starts or ends within this range.
		    Arguments.of(1, 0, LocalDateTime.of(2025, 3, 2, 14, 30), LocalDateTime.of(2025, 3, 2, 17, 00)),

		    // 16:00 - 17:00 → Se busca en un período vacío entre dos reservas activas sin tocarlas.
		    // No debería devolver ninguna reserva.
		    //
		    // 16:00 - 17:00 → Searching in an empty period between two active bookings without touching them.
		    // Should return no bookings.
		    Arguments.of(1, 0, LocalDateTime.of(2025, 3, 2, 16, 0), LocalDateTime.of(2025, 3, 2, 17, 00)),

		    // 13:00 - 22:00 (aula 2) → Se busca en otra aula donde NO hay reservas activas.
		    //
		    // 13:00 - 22:00 (classroom 2) → Searching in a different classroom with NO active bookings.
		    Arguments.of(2, 0, LocalDateTime.of(2025, 3, 2, 13, 0), LocalDateTime.of(2025, 3, 2, 22, 00))
		);

	}

	// Configura reservas de prueba para findBookingsForClassroomByPeriod_BookingsWithinPeriodTest.
	//
	// It sets test bookings for findBookingsForClassroomByPeriod_BookingsWithinPeriodTest.
	private void setupTestBookings() {
		Booking booking1 = new Booking(0, 1, 10, LocalDateTime.of(2025, 3, 2, 14, 0),
				LocalDateTime.of(2025, 3, 2, 15, 30), LocalDateTime.now(), "Test booking 1", BookingStatus.ACTIVE);
		bookingRepository.save(booking1);

		Booking booking2 = new Booking(0, 1, 10, LocalDateTime.of(2025, 3, 2, 17, 0),
				LocalDateTime.of(2025, 3, 2, 18, 0), LocalDateTime.now(), "Test booking 2", BookingStatus.ACTIVE);
		bookingRepository.save(booking2);

		Booking booking3 = new Booking(0, 1, 10, LocalDateTime.of(2025, 3, 2, 19, 0),
				LocalDateTime.of(2025, 3, 2, 20, 30), LocalDateTime.now(), "Test booking 3", BookingStatus.CANCELLED);
		bookingRepository.save(booking3);

		Booking booking4 = new Booking(0, 4, 10, LocalDateTime.of(2025, 3, 2, 17, 30),
				LocalDateTime.of(2025, 3, 2, 18, 30), LocalDateTime.now(), "Test booking 4", BookingStatus.ACTIVE);
		bookingRepository.save(booking4);

		entityManager.flush();
		entityManager.clear();
	}
	
	// Este test usa dos métodos auxiliares:
	// (1) setupTestBookings() → Configura las reservas de prueba.
	// (2) queriesByPeriod() → Proporciona diferentes períodos de búsqueda.
	//
	// This test uses two auxiliary methods:
	// (1) setupTestBookings() → Sets up test bookings.
	// (2) queriesByPeriod() → Provides different search periods.
	@ParameterizedTest
	@MethodSource("queriesByPeriod")
	@DisplayName("Should return no bookings as classroom has none")
	void findBookingsForClassroomByPeriod_BookingsClassroomNoBookings(int idClassroom, 
			int totalValidBookings, LocalDateTime queryStart, LocalDateTime queryFinish) {
		// Arrange
		setupTestBookings();

		// Act
		List<Booking> bookingsFound = bookingRepository.findActiveBookingsForClassroomByPeriod(idClassroom,
				queryStart, queryFinish);

		// Assert
		assertEquals(totalValidBookings, bookingsFound.size(), 
				"Expected " + totalValidBookings + " active bookings for classroom " + idClassroom +
				" between " + queryStart + " and " + queryFinish + ", but found " + bookingsFound.size());
	}

}
