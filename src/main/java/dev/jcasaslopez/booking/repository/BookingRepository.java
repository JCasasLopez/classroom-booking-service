package dev.jcasaslopez.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Modifying
	@Query("UPDATE Booking b SET b.status = :status WHERE b.idBooking = :idBooking")
	void cancelBooking(Long idBooking, BookingStatus status);

	@Modifying
	@Query("UPDATE Booking b SET b.status = 'COMPLETED' WHERE b.status = 'ACTIVE' AND b.finish < :now")
	void markCompletedBookings(LocalDateTime now);

	// Este método busca reservas activas en un aula durante un período específico,
	// excluyendo un estado determinado para evitar conflictos al realizar una nueva reserva.
	//
	// This method retrieves active bookings for a classroom within a specified period,
	// excluding a given status to prevent conflicts when making a new reservation.
	@Query("""
		    SELECT b FROM Booking b
		    WHERE b.idClassroom = :queryIdClassroom
		    AND b.status = 'ACTIVE'
		    AND b.start >= :queryStart
		    AND b.finish <= :queryFinish
		""")
		List<Booking> findActiveBookingsForClassroomByPeriod(int queryIdClassroom, LocalDateTime queryStart,
		        LocalDateTime queryFinish);

	// Devuelve una lista de aulas ocupadas en un período específico, basándose en reservas activas.
	//
	// Returns a list of occupied classrooms within a given period, based on active bookings.
	@Query(value = """
			SELECT DISTINCT b.idClassroom
			FROM bookings b
			WHERE b.status = 'ACTIVE'
			AND (
			(b.start < :queryFinish AND b.finish > :queryStart)
			)
			""", nativeQuery = true)
	List<Integer> findOccupiedClassroomsbyPeriod(LocalDateTime queryStart, LocalDateTime queryFinish);
	
	// Recupera todas las reservas de un usuario, sin importar su estado (activas, canceladas o completadas),
	// ordenadas de la más reciente a la más antigua.
	//
	// Retrieves all bookings made by a user, including active, cancelled, and completed ones,
	// ordered from most recent to oldest.
	@Query("""
		       SELECT b FROM Booking b
		       WHERE b.idUser = :queryIdUser
		       ORDER BY b.start DESC
		       """)
		List<Booking> findBookingsByUser(int queryIdUser);
	
}
