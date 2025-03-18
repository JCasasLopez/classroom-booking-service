package dev.jcasaslopez.booking.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.enums.NotificationType;
import dev.jcasaslopez.booking.exception.NoSuchClassroomException;
import dev.jcasaslopez.booking.model.ClassroomsList;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
	
	@InjectMocks
    private NotificationServiceImpl notificationServiceImpl;
    
    @Mock
    private ClassroomsList classroomsList; 

    @BeforeEach
    void setUp() {
        List<ClassroomDto> classroomMocks = Arrays.asList(
                new ClassroomDto(1, "101", 100, true, true),
                new ClassroomDto(2, "102", 50, true, false),
                new ClassroomDto(3, "103", 30, false, true),
                new ClassroomDto(4, "104", 20, false, false)
            );
        Mockito.when(classroomsList.getClassroomsList()).thenReturn(classroomMocks);
    }
	
    @Test
	@DisplayName("messageBuilder() should return correct message for booking")
	public void messageBuilder_WhenBooking_ShouldReturnCorrectMessageTest() {
		// Arrange
		int idUser = 1;
		
		// Act
		Map<String, String> message = notificationServiceImpl.messageBuilder(NotificationType.BOOK, idUser, 1, 
				LocalDateTime.of(2025, 3, 17, 17, 0), LocalDateTime.of(2025, 3, 17, 17, 30));
		
		// Assert
		assertAll( 	
					() -> assertEquals(String.valueOf(idUser), message.get("Recipient")),
					() -> assertEquals("Booking confirmed", message.get("Subject")),
					() -> assertEquals("We are pleased to confirm your booking for classroom 101 on Monday"
							+ ", March 17, 2025 from 17:00 to 17:30.",
							message.get("Message")
							)
				);
	}
    
    @Test
	@DisplayName("messageBuilder() should return correct message for a watch alert")
	public void messageBuilder_WhenReceivingWatchAlert_ShouldReturnCorrectMessageTest() {
		// Arrange
		int idUser = 1;
		
		// Act
		Map<String, String> message = notificationServiceImpl.messageBuilder(NotificationType.WATCH_ALERT, 
				idUser, 1, LocalDateTime.of(2025, 3, 17, 17, 0), LocalDateTime.of(2025, 3, 17, 17, 30));
		
		// Assert
		assertAll( 	
					() -> assertEquals(String.valueOf(idUser), message.get("Recipient")),
					() -> assertEquals("A spot you were watching has opened up!", message.get("Subject")),
					() -> assertEquals("A booking for classroom 101 on Monday, March 17, 2025 from 17:00"
							+ " to 17:30 has been cancelled. Hurry up and book it before someone else does!",
							message.get("Message")
							)
				);
	}
	
	@ParameterizedTest
	@CsvSource({
		"1, 101",
		"2, 102",
		"3, 103",
		"4, 104"
	})
	@DisplayName("getClassroomName() should return correct classroom name")
	public void getClassroomName_WhenClassroomExists_ShouldReturnExpectedNameTest(int idClassroom, 
			String expectedClassroomName) {
		// Arrange
		
		// Act
		String classroomName = notificationServiceImpl.getClassroomName(idClassroom);
		
		// Assert
		assertEquals(expectedClassroomName, classroomName);
		
	}
	
	@Test
	@DisplayName("getClassroomName() should throw an exception when classroom does not exist")
	public void getClassroomName_WhenClassroomDoesNotExist_ShouldThrowExceptionTest() {
		// Arrange
		int idClassroom = 107;
		
		// Act & Assert
		assertThrows(NoSuchClassroomException.class,  
				() -> notificationServiceImpl.getClassroomName(idClassroom), 
				"It should throw a NoSuchClassroomException when classroom does not exist");
	
	}
	

}
