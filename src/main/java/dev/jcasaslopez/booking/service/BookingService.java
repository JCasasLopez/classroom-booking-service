package dev.jcasaslopez.booking.service;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.enums.BookingStatus;

public interface BookingService {
	
	void createBooking(BookingDto bookingDto);
	void cancelBooking (Long idBooking, BookingStatus bookingStatus);
	
}
