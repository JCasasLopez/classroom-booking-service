package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;

public interface BookingService {
	
	BookingDto book(BookingDto bookingDto);
	void cancel(Long idBooking, BookingStatus bookingStatus);
	List<Booking> bookingsByUser(int idUser);
	void markBookingsAsCompleted(LocalDateTime now);
	
}
