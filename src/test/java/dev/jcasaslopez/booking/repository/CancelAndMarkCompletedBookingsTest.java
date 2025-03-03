package dev.jcasaslopez.booking.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;
import jakarta.persistence.EntityManager;

@DataJpaTest
public class CancelAndMarkCompletedBookingsTest {
	
	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	// Método auxiiar para reducir el código repetido.
	// Auxiliary method to reduce boilerplate code.
	private Booking createAndSaveBooking(LocalDateTime start, LocalDateTime finish, BookingStatus status) {
		Booking booking = new Booking(0, 100, 200, start, finish, LocalDateTime.now(), "Test booking", status);
		Booking savedBooking = bookingRepository.save(booking);
		// Sincronizamos los cambios en memoria con la base de datos.
		// We synchronize the in-memory changes with the database.
		entityManager.flush();
		return savedBooking;
	}
	
	// Método auxiiar para reducir el código repetido.
	// Auxiliary method to reduce boilerplate code.
	private void executeFlushAndClear(Runnable action) {
		action.run();
		// Sincronizamos los cambios en memoria con la base de datos.
		// We synchronize the in-memory changes with the database.
		entityManager.flush();
		// Limpiamos el contexto de persistencia (memoria), asegurándonos de que cuando
		// se recupere el objeto este será el que está en la base de datos.
		// We clear the persistence context (memory), ensuring that when the object is
		// retrieved it will be the one stored in the database.
		entityManager.clear();
	}
	
	// Método auxiiar para reducir el código repetido.
	// Auxiliary method to reduce boilerplate code.
	private void assertBookingStatus(Long bookingId, BookingStatus expectedStatus, String message) {
		// Hay que recuperar otra vez el objeto de la base de datos para verificar el
		// cambio.
		// We need to retrieve the object again from the database to verify the change.
        Optional<Booking> updatedBooking = bookingRepository.findById(bookingId);
        assertTrue(updatedBooking.isPresent(), "The booking should exist");
        assertEquals(expectedStatus, updatedBooking.get().getStatus(), message);
    }
	
	@Test
    @DisplayName("Should cancel a booking successfully when a valid ID is provided")
    void cancelBooking_WithValidID_BookingCancelledSuccessfully() {
        // Arrange
		Booking savedBooking = createAndSaveBooking(
                LocalDateTime.of(2025, 3, 1, 10, 0),
                LocalDateTime.of(2025, 3, 1, 12, 0),
                BookingStatus.ACTIVE
        );
        Long bookingId = savedBooking.getIdBooking();

        // Act
        executeFlushAndClear(() -> bookingRepository.cancelBooking(bookingId, BookingStatus.CANCELLED));
        
        // Assert
        assertBookingStatus(bookingId, BookingStatus.CANCELLED, 
        		"The booking status should be CANCELLED");
    }
	
	@Test
    @DisplayName("Should mark past bookings as COMPLETED")
    void markCompletedBookings_ChangesPastActiveBookingsToCompleted() {
		// Arrange
        Booking savedBooking = createAndSaveBooking(
                LocalDateTime.now().minusHours(2).withMinute(30),
                LocalDateTime.now().minusHours(1).withMinute(30),
                BookingStatus.ACTIVE
        );
        Long bookingId = savedBooking.getIdBooking();

        // Act
        executeFlushAndClear(() -> bookingRepository.markCompletedBookings(LocalDateTime.now()));

        // Assert
        assertBookingStatus(bookingId, BookingStatus.COMPLETED, "The booking status should be COMPLETED");
    }
	
	@Test
	@DisplayName("Should not mark future bookings as COMPLETED")
	void markCompletedBookings_DoesNotChangeFutureBookings() {
		// Arrange
		Booking savedBooking = createAndSaveBooking(
				LocalDateTime.now().plusHours(1).withMinute(30),
				LocalDateTime.now().plusHours(2).withMinute(30), 
                BookingStatus.ACTIVE
        );
		Long bookingId = savedBooking.getIdBooking();

		// Act
        executeFlushAndClear(() -> bookingRepository.markCompletedBookings(LocalDateTime.now()));

		// Assert
        assertBookingStatus(bookingId, BookingStatus.ACTIVE, "The booking status should be ACTIVE");
	}
	
	@Test
	@DisplayName("Should not mark ongoing bookings as COMPLETED")
	void markCompletedBookings_DoesNotChangeOngoingBookings() {
		Booking savedBooking = createAndSaveBooking(
				LocalDateTime.now().minusHours(1).withMinute(30),
				LocalDateTime.now().plusHours(1).withMinute(30), 
                BookingStatus.ACTIVE
        );
		// Arrange
		Long bookingId = savedBooking.getIdBooking();

		// Act
        executeFlushAndClear(() -> bookingRepository.markCompletedBookings(LocalDateTime.now()));

		// Assert
        assertBookingStatus(bookingId, BookingStatus.ACTIVE, "The booking status should be ACTIVE");
	}
	
	@Test
	@DisplayName("Should not change already completed bookings")
	void markCompletedBookings_DoesNotChangeAlreadyCompletedBookings() {
		// Arrange
		Booking savedBooking = createAndSaveBooking(
				LocalDateTime.now().minusHours(2).withMinute(30),
				LocalDateTime.now().minusHours(1).withMinute(30), 
                BookingStatus.COMPLETED
        );
		Long bookingId = savedBooking.getIdBooking();

		// Act
        executeFlushAndClear(() -> bookingRepository.markCompletedBookings(LocalDateTime.now()));

		// Assert
        assertBookingStatus(bookingId, BookingStatus.COMPLETED, "The booking status should be COMPLETED");

	}
	
	@Test
	@DisplayName("Should not affect cancelled bookings")
	void markCompletedBookings_DoesNotAffectCancelledBookings() {
		// Arrange
		Booking savedBooking = createAndSaveBooking(
				LocalDateTime.now().plusHours(1).withMinute(30),
				LocalDateTime.now().plusHours(2).withMinute(30), 
                BookingStatus.CANCELLED
        );
		Long bookingId = savedBooking.getIdBooking();

		// Act
        executeFlushAndClear(() -> bookingRepository.markCompletedBookings(LocalDateTime.now()));

		// Assert
        assertBookingStatus(bookingId, BookingStatus.CANCELLED, "The booking status should be CANCELLED");

	}
	
}