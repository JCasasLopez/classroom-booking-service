package dev.jcasaslopez.booking.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import dev.jcasaslopez.booking.entity.WatchAlert;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WatchAlertRepositoryTest {
	
	@Autowired
	private WatchAlertRepository watchAlertRepository;
	
	@BeforeAll 
	void setUpWatchAlerts() {
			
		List<WatchAlert> watchAlerts = List.of(
	            new WatchAlert(0, 101, 1, LocalDateTime.of(2025, 3, 17, 8, 0), 
	                           LocalDateTime.of(2025, 3, 17, 8, 30), LocalDateTime.now()),
	            new WatchAlert(0, 101, 1, LocalDateTime.of(2025, 3, 18, 14, 0), 
	                           LocalDateTime.of(2025, 3, 18, 14, 30), LocalDateTime.now()),
	            new WatchAlert(0, 101, 3, LocalDateTime.of(2025, 3, 18, 10, 0), 
	                           LocalDateTime.of(2025, 3, 18, 10, 30), LocalDateTime.now()),
	            new WatchAlert(0, 102, 3, LocalDateTime.of(2025, 3, 20, 16, 0), 
	                           LocalDateTime.of(2025, 3, 20, 16, 30), LocalDateTime.now()),
	            new WatchAlert(0, 104, 3, LocalDateTime.of(2025, 3, 19, 11, 0), 
	                           LocalDateTime.of(2025, 3, 19, 11, 30), LocalDateTime.now()),
	            new WatchAlert(0, 104, 4, LocalDateTime.of(2025, 3, 21, 9, 0), 
	                           LocalDateTime.of(2025, 3, 21, 9, 30), LocalDateTime.now()),
	            new WatchAlert(0, 105, 1, LocalDateTime.of(2025, 3, 20, 14, 0), 
	                           LocalDateTime.of(2025, 3, 20, 14, 30), LocalDateTime.now()),
	            new WatchAlert(0, 105, 5, LocalDateTime.of(2025, 3, 21, 17, 0), 
	                           LocalDateTime.of(2025, 3, 21, 17, 30), LocalDateTime.now())
	        );
		
		watchAlertRepository.saveAll(watchAlerts);
	}
	
	@ParameterizedTest
	// idUser, number of watch alerts expected.
	@CsvSource({
		"1, 3",
		"2, 0",
		"3, 3",
		"4, 1",
		"5, 1"
	})
	@DisplayName("findWatchAlertsByUser() should return the correct list of WatchAlerts")
	public void findWatchAlertsByUser_ShouldReturnExpectedResult(int idUser, int expectedWatchAlerts) {
		// Arrange
		
		// Act
		List<WatchAlert> watchAlertsList = watchAlertRepository.findWatchAlertsByUser(idUser);
		
		// Assert
		assertEquals(expectedWatchAlerts, watchAlertsList.size(), "Number of watch alerts expected was "
				+ expectedWatchAlerts + " but actual number was " + watchAlertsList.size());
			
	}
	
	public static Stream<Arguments> findWatchAlertsByTimePeriodAndClassroomData(){
		// Argument: start, finish, idClassroom, number of watch alerts expected.
		return Stream.of(
				// Edge cases: start and finish coinciden con el inicio y final de alertas 
				// (por lo tanto ambas deben incluirse en la lista).
				// 
				// Edge cases: start and finish coincide with beginning and end of alerts (hence
				// both of them should be included in the returned list).
				Arguments.of(LocalDateTime.of(2025, 3, 17, 8, 0), LocalDateTime.of(2025, 3, 18, 14, 30),
						101, 3),
				
				// Edge cases: start and finish coinciden con el final e inicio de alertas 
				// (por tanto ninguna de ellas deben incluirse en la lista).
				// 
				// Edge cases: start and finish coincide with end and beginning of alerts (hence
				// neither should be included in the returned list).
				Arguments.of(LocalDateTime.of(2025, 3, 17, 8, 30), LocalDateTime.of(2025, 3, 18, 14, 0),
						101, 1),
				
				// No hay alertas para este aula.
				//
				// No watch alerts for this classroom.
				Arguments.of(LocalDateTime.of(2025, 3, 21, 18, 0), LocalDateTime.of(2025, 3, 21, 18, 30),
						103, 0),
				
				Arguments.of(LocalDateTime.of(2025, 3, 18, 18, 0), LocalDateTime.of(2025, 3, 21, 18, 30),
						102, 1)
				);
	}
	
	@ParameterizedTest
	@MethodSource("findWatchAlertsByTimePeriodAndClassroomData")
	@DisplayName("findWatchAlertsByTimePeriodAndClassroom should return the correct list of WatchAlerts")
	public void findWatchAlertsByTimePeriodAndClassroom_ShouldReturnExpectedResult(
			LocalDateTime start, LocalDateTime finish, int idClassroom, int expectedWatchAlerts) {
		// Arrange
		
		// Act
		List<WatchAlert> watchAlertsList = watchAlertRepository.
				findWatchAlertsByTimePeriodAndClassroom(idClassroom, start, finish);
		
		// Assert
		assertEquals(expectedWatchAlerts, watchAlertsList.size(), "Number of watch alerts expected was "
				+ expectedWatchAlerts + " but actual number was " + watchAlertsList.size());	
	}
}
