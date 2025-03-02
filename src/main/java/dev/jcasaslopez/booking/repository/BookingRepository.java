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

	@Query("""
			SELECT b FROM Booking b
			WHERE b.idClassroom = :queryIdClassroom
			AND b.status <> :excludedStatus
			AND (
			    (:queryStart BETWEEN b.start AND b.finish)
			    OR (:queryFinish BETWEEN b.start AND b.finish)
			    OR (b.start BETWEEN :queryStart AND :queryFinish)
			)
			""")
	List<Booking> findBookingsForClassroomByPeriod(int queryIdClassroom, LocalDateTime queryStart,
			LocalDateTime queryFinish, BookingStatus excludedStatus);

	@Query(value = """
		    SELECT DISTINCT b.idClassroom 
		    FROM bookings b
		    WHERE b.status = 'ACTIVE'
		      AND ((:queryStart BETWEEN b.start AND b.finish)
		      OR (:queryFinish BETWEEN b.start AND b.finish)
		      OR (b.start BETWEEN :queryStart AND :queryFinish))
		    """, nativeQuery = true)
	List<Integer> findOccupiedClassroomsbyPeriod(LocalDateTime queryStart, LocalDateTime queryFinish);
	
}
