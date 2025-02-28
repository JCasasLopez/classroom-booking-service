package dev.jcasaslopez.booking.mapper;

import org.springframework.stereotype.Component;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;

@Component
public class BookingMapper {
	
	public BookingDto bookingToBookingDto(Booking booking) {
		return new BookingDto(booking.getIdBooking(),
				booking.getIdClassroom(),
				booking.getIdUser(),
				booking.getStart(),
				booking.getFinish(),
				booking.getTimestamp(),
				booking.getComment(),
				booking.getStatus());
	}

	public Booking bookingDtoToBooking(BookingDto booking) {
		return new Booking(0,
				booking.getIdClassroom(),
				booking.getIdUser(),
				booking.getStart(),
				booking.getFinish(),
				booking.getTimestamp(),
				booking.getComment(),
				booking.getStatus());
	} 

}
