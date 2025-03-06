package dev.jcasaslopez.booking.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.exception.NoSuchBookingException;
import dev.jcasaslopez.booking.mapper.BookingMapper;
import dev.jcasaslopez.booking.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {
	
	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	private BookingRepository bookingRepository;
	private BookingMapper bookingMapper; 
	
	public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper) {
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
	}

	@Override
	public BookingDto book(BookingDto bookingDto) {
		logger.info("Creating new booking: Booking ID= {}, User ID= {}, Start= {}, Finish= {}", bookingDto.getIdClassroom(),
				bookingDto.getIdUser(), bookingDto.getStart(), bookingDto.getFinish());
		Booking returnedBooking = bookingRepository.save(bookingMapper.bookingDtoToBooking(bookingDto));
		logger.info("Booking created: Booking ID= {}, User ID= {}, Start= {}, Finish= {}", returnedBooking.getIdClassroom(),
				returnedBooking.getIdUser(), returnedBooking.getStart(), returnedBooking.getFinish());
		return bookingMapper.bookingToBookingDto(returnedBooking);
	}

	@Override
	public void cancel(Long idBooking, BookingStatus bookingStatus) {
		logger.info("Attempting to cancel booking with ID: {}", idBooking);
		Optional<Booking> foundBooking = bookingRepository.findById(idBooking);
		if(foundBooking.isEmpty()) {
            logger.warn("Booking not found with ID: {}", idBooking);
			throw new NoSuchBookingException("No such booking or incorrect idBooking");
		}
		bookingRepository.cancelBooking(idBooking, bookingStatus);
        logger.info("Booking cancelled successfully with ID: {}", idBooking);
	}

}
