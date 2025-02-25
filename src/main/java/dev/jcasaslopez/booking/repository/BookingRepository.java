package dev.jcasaslopez.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import dev.jcasaslopez.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Modifying
	@Query("UPDATE Booking b SET b.status = 'CANCELLED' WHERE b.idBooking = :idBooking")
	void cancelBooking(Long idBooking);

	@Modifying
	@Query("UPDATE Booking b SET b.status = 'COMPLETED' WHERE b.status = 'ACTIVE' AND b.finish < :now")
	void markCompletedBookings(LocalDateTime now);

	@Query(value = """
		    SELECT * FROM bookings 
		    WHERE idClassroom = :queryIdClassroom 
		    AND ((:queryStart BETWEEN start AND finish)
		    OR (:queryFinish BETWEEN start AND finish)
		    OR (start BETWEEN :queryStart AND :queryFinish))
		    """, nativeQuery = true)
	List<Booking> findBookingsForClassroomByPeriod(int queryIdClassroom, LocalDateTime queryStart, 
			LocalDateTime queryFinish);

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
