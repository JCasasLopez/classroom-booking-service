package dev.jcasaslopez.booking.service;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.enums.BookingStatus;

public interface BookingService {
	
	BookingDto book(BookingDto bookingDto);
	void cancel(Long idBooking, BookingStatus bookingStatus);
	
}
