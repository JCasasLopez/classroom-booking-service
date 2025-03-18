package dev.jcasaslopez.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.entity.WatchAlert;
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.enums.NotificationType;
import dev.jcasaslopez.booking.exception.ClassroomNotAvailableException;
import dev.jcasaslopez.booking.exception.NoSuchBookingException;
import dev.jcasaslopez.booking.mapper.BookingMapper;
import dev.jcasaslopez.booking.repository.BookingRepository;
import dev.jcasaslopez.booking.repository.WatchAlertRepository;
import dev.jcasaslopez.booking.slot.SlotManagerImpl;

@ExtendWith(MockitoExtension.class)
public class BookingServiceMockTest {
	
	@InjectMocks
	@Spy
	private BookingServiceImpl bookingServiceImpl;
	
	@Mock
	private SlotManagerImpl slotManagerImpl;
	
	@Mock
	private BookingRepository bookingRepository;
	
	@Mock
	private NotificationServiceImpl notificationServiceImpl;
	
	@Mock
	private BookingMapper bookingMapper;
	
	@Mock
	private WatchAlertRepository watchAlertRepository;
	
	@Test
	@DisplayName("book() creates a booking successfully when the classroom is available")
	public void book_WhenClassroomIsAvailable_CreatesBookingSuccessfullyTest() {
		// Arrange
		BookingDto bookingDto = new BookingDto(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		Booking booking = new Booking(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		when(slotManagerImpl.isClassroomAvailableDuringPeriod(bookingDto.getIdClassroom(), bookingDto.getStart(),
				bookingDto.getFinish())).thenReturn(true);
		when(bookingMapper.bookingDtoToBooking(bookingDto)).thenReturn(booking);
		when(bookingRepository.save(booking)).thenReturn(booking);
		when(bookingMapper.bookingToBookingDto(booking)).thenReturn(bookingDto);
	
		// Act 
		BookingDto returnedBooking = bookingServiceImpl.book(bookingDto);
		
		// Assert
		InOrder inOrder = inOrder(slotManagerImpl, bookingMapper, bookingRepository);
		inOrder.verify(slotManagerImpl).isClassroomAvailableDuringPeriod(bookingDto.getIdClassroom(), 
				bookingDto.getStart(), bookingDto.getFinish());
		inOrder.verify(bookingMapper).bookingDtoToBooking(bookingDto);
		inOrder.verify(bookingRepository).save(booking);
		inOrder.verify(bookingMapper).bookingToBookingDto(booking);
		assertEquals(bookingDto, returnedBooking, "The returned booking does not match the expected "
				+ "one. Expected: " + bookingDto + " but got: " + returnedBooking);
	}
	
	@Test
	@DisplayName("book() throws an exception when the classroom is NOT available")
	public void book_WhenClassroomIsNotAvailable_ThrowsExceptionTest() {
		// Arrange
		BookingDto bookingDto = new BookingDto(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		when(slotManagerImpl.isClassroomAvailableDuringPeriod(bookingDto.getIdClassroom(), bookingDto.getStart(),
				bookingDto.getFinish())).thenReturn(false);
	
		// Act and Assert
		assertThrows(ClassroomNotAvailableException.class, () -> bookingServiceImpl.book(bookingDto),
				"Expected ClassroomNotAvailableException, but no exception was thrown.");
		
		verify(slotManagerImpl).isClassroomAvailableDuringPeriod(bookingDto.getIdClassroom(), 
				bookingDto.getStart(), bookingDto.getFinish());
	}
	
	@Test
	@DisplayName("cancel() cancels a booking successfully when idBooking is correct")
	public void cancel_WhenIdBookingIsCorrect_CancelsBookingSuccessfullyTest() {
		// Arrange
		Long idBooking = 1L;
		
		Booking booking = new Booking(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		when(bookingRepository.findById(idBooking)).thenReturn(Optional.of(booking));
		doNothing().when(bookingServiceImpl).notifyUsersAboutCancellation(idBooking);
	
		// Act 
		bookingServiceImpl.cancel(1L, BookingStatus.CANCELLED);
		
		// Assert
		InOrder inOrder = inOrder(bookingRepository, bookingServiceImpl);
		inOrder.verify(bookingRepository).findById(idBooking);
		inOrder.verify(bookingRepository).cancelBooking(1L, BookingStatus.CANCELLED);
		inOrder.verify(bookingServiceImpl).notifyUsersAboutCancellation(1L);
	}
	
	@Test
	@DisplayName("cancel() throws an exception when idBooking is NOT correct")
	public void cancel_WhenIdBookingIsNotCorrect_ThrowsAnExceptionTest() {
		// Arrange
		Long idBooking = 1L;
		when(bookingRepository.findById(idBooking)).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(NoSuchBookingException.class, 
				() -> bookingServiceImpl.cancel(1L, BookingStatus.CANCELLED),
				"Expected NoSuchBookingException, but no exception was thrown.");	
	}
	
	@Test
	@DisplayName("notifyUsersAboutCancellation() runs without sending notifications when no alerts exist")
	public void notifyUsersAboutCancellation_WhenNoAlerts_NoNotificationsSentTest() {
		// Arrange
		Long idBooking = 1L;
		
		Booking cancelledBooking = new Booking(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		when(bookingRepository.findById(idBooking)).thenReturn(Optional.of(cancelledBooking));
		when(watchAlertRepository.findWatchAlertsByTimePeriodAndClassroom
		(cancelledBooking.getIdClassroom(), cancelledBooking.getStart(), 
				cancelledBooking.getFinish())).thenReturn(Collections.emptyList());
	
		// Act 
		bookingServiceImpl.notifyUsersAboutCancellation(1L);
		
		// Assert
		InOrder inOrder = inOrder(bookingRepository, watchAlertRepository, notificationServiceImpl);
		inOrder.verify(bookingRepository).findById(idBooking);
		inOrder.verify(watchAlertRepository).findWatchAlertsByTimePeriodAndClassroom
				(cancelledBooking.getIdClassroom(), cancelledBooking.getStart(), cancelledBooking.getFinish());
	}
	
	@Test
	@DisplayName("notifyUsersAboutCancellation() works as expected when there are alerts")
	public void notifyUsersAboutCancellation_WhenAlerts_SendNotificationsTest() {
		// Arrange
		Long idBooking = 1L;
		
		Booking cancelledBooking = new Booking(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            );
		
		WatchAlert watchAlert = new WatchAlert(
			    1L,  
			    101, 
			    200, 
			    LocalDateTime.of(2025, 3, 17, 9, 0),  
			    LocalDateTime.of(2025, 3, 17, 9, 30), 
			    LocalDateTime.now() 
			);

		
		when(bookingRepository.findById(idBooking)).thenReturn(Optional.of(cancelledBooking));
		when(watchAlertRepository.findWatchAlertsByTimePeriodAndClassroom
		(cancelledBooking.getIdClassroom(), cancelledBooking.getStart(), 
				cancelledBooking.getFinish())).thenReturn(List.of(watchAlert));
	
		// Act 
		bookingServiceImpl.notifyUsersAboutCancellation(1L);
		
		// Assert
		InOrder inOrder = inOrder(bookingRepository, watchAlertRepository, notificationServiceImpl);
		inOrder.verify(bookingRepository).findById(idBooking);
		inOrder.verify(watchAlertRepository).findWatchAlertsByTimePeriodAndClassroom
				(cancelledBooking.getIdClassroom(), cancelledBooking.getStart(), cancelledBooking.getFinish());
		inOrder.verify(notificationServiceImpl).sendNotification(NotificationType.WATCH_ALERT,
				watchAlert.getIdUser(), watchAlert.getIdClassroom(), watchAlert.getStart(), watchAlert.getFinish());
	}
	
}
