package dev.jcasaslopez.booking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.jcasaslopez.booking.entity.WatchAlert;

public interface WatchAlertRepository extends JpaRepository<WatchAlert, Long> {
	
	@Query("SELECT w FROM WatchAlert w WHERE w.idUser = :idUser")
	List<WatchAlert> findWatchAlertsByUser(int idUser);
	
    @Query("SELECT w FROM WatchAlert w WHERE w.idClassroom = :idClassroom AND w.start >= :start "
    		+ "AND w.finish <= :finish")
	List<WatchAlert> findWatchAlertsByTimePeriodAndClassroom(int idClassroom, LocalDateTime start, 
			LocalDateTime finish);
}
