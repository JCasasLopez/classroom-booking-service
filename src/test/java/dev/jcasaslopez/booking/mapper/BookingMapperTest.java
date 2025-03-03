package dev.jcasaslopez.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;

// Estas anotaciones permiten evitar la carga completa del contexto con @SpringBootTest.  
// En su lugar, inicializamos solo ClassroomMapper y la configuración mínima necesaria.  
//
// Instead of loading the full context with @SpringBootTest, we initialize only  
// BookingMapper and the minimal required configuration.
@ExtendWith(SpringExtension.class)
@Import(BookingMapper.class)
class BookingMapperTest {
	
	@Autowired
	private BookingMapper bookingMapper;

	@Test
	@DisplayName("BookingToBookingDto should return a BookingDto successfully")
	void bookingToBookingDto_returnsBookingDto() {
		// Arrange
		Booking booking = new Booking(0, 1, 10, LocalDateTime.of(2025, 3, 2, 14, 0), 
				LocalDateTime.of(2025, 3, 2, 15, 30), LocalDateTime.now(), 
				"Test booking", BookingStatus.ACTIVE);
		
		// Act
		BookingDto mappedBookingDto = bookingMapper.bookingToBookingDto(booking);
		
		// Assert
		assertAll(
		        () -> assertEquals(booking.getIdBooking(), mappedBookingDto.getIdBooking(), 
		        		"IdBookings should match"),
		        () -> assertEquals(booking.getIdClassroom(), mappedBookingDto.getIdClassroom(), 
		        		"IdClassrooms should match"),
		        () -> assertEquals(booking.getIdUser(), mappedBookingDto.getIdUser(), 
		        		"IdUsers should match"),
		        () -> assertEquals(booking.getStart(), mappedBookingDto.getStart(), 
		        		"Start times should match"),
		        () -> assertEquals(booking.getFinish(), mappedBookingDto.getFinish(), 
		        		"Finish times should match"),
		        () -> assertTrue(ChronoUnit.SECONDS.between(booking.getTimestamp(), 
		        		mappedBookingDto.getTimestamp()) < 2, "Timestamps should be close"),
		        () -> assertEquals(booking.getComment(), mappedBookingDto.getComment(), 
		        		"Comments should match"),
		        () -> assertEquals(booking.getStatus(), mappedBookingDto.getStatus(), 
		        		"Status should match")
		    );
	}
	
	@Test
	@DisplayName("BookingDtoToBooking should return a Booking successfully")
	void bookingDtoToBooking_returnsBooking() {
		// Arrange
		BookingDto bookingDto = new BookingDto(0, 1, 10, LocalDateTime.of(2025, 3, 2, 14, 0), 
				LocalDateTime.of(2025, 3, 2, 15, 30), LocalDateTime.now(), 
				"Test booking", BookingStatus.ACTIVE);

		// Act
		Booking mappedBooking = bookingMapper.bookingDtoToBooking(bookingDto);

		// Assert
		assertAll(
		        () -> assertEquals(bookingDto.getIdBooking(), mappedBooking.getIdBooking(), 
		        		"IdBookings should match"),
		        () -> assertEquals(bookingDto.getIdClassroom(), mappedBooking.getIdClassroom(), 
		        		"IdClassrooms should match"),
		        () -> assertEquals(bookingDto.getIdUser(), mappedBooking.getIdUser(), 
		        		"IdUsers should match"),
		        () -> assertEquals(bookingDto.getStart(), mappedBooking.getStart(), 
		        		"Start times should match"),
		        () -> assertEquals(bookingDto.getFinish(), mappedBooking.getFinish(), 
		        		"Finish times should match"),
		        () -> assertTrue(ChronoUnit.SECONDS.between(bookingDto.getTimestamp(), 
		        		mappedBooking.getTimestamp()) < 2, "Timestamps should be close"),
		        () -> assertEquals(bookingDto.getComment(), mappedBooking.getComment(), 
		        		"Comments should match"),
		        () -> assertEquals(bookingDto.getStatus(), mappedBooking.getStatus(), 
		        		"Status should match")
		    );
	}

}
