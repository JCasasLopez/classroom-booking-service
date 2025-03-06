package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.exception.NoSuchBookingException;
import dev.jcasaslopez.booking.mapper.BookingMapper;
import dev.jcasaslopez.booking.repository.BookingRepository;

// Esta clase no contiene lógica de negocio, solo delega llamadas a repositorios y mapeadores. 
// Por esta razón, no se realizarán tests unitarios sobre ella. 
// El comportamiento se validará a nivel de integración. 
// 
// This class does not contain business logic; it only delegates calls to repositories and mappers. 
// For this reason, unit tests will not be written for it. 
// The behavior will be validated through integration tests.
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
	@Transactional
	public BookingDto book(BookingDto bookingDto) {
		logger.info("Creating new booking: Booking ID= {}, User ID= {}, Start= {}, Finish= {}", bookingDto.getIdClassroom(),
				bookingDto.getIdUser(), bookingDto.getStart(), bookingDto.getFinish());
		Booking returnedBooking = bookingRepository.save(bookingMapper.bookingDtoToBooking(bookingDto));
		logger.info("Booking created: Booking ID= {}, User ID= {}, Start= {}, Finish= {}", returnedBooking.getIdClassroom(),
				returnedBooking.getIdUser(), returnedBooking.getStart(), returnedBooking.getFinish());
		return bookingMapper.bookingToBookingDto(returnedBooking);
	}

	@Override
	@Transactional
	public void cancel(Long idBooking, BookingStatus bookingStatus) {
	    logger.info("Attempting to cancel booking with ID: {}", idBooking);
	    bookingRepository.findById(idBooking)
	        .orElseThrow(() -> {
	            logger.warn("Booking not found with ID: {}", idBooking);
	            return new NoSuchBookingException("No such booking or incorrect idBooking");
	        });
	    bookingRepository.cancelBooking(idBooking, bookingStatus);
	    logger.info("Booking cancelled successfully with ID: {}", idBooking);
	}

	@Override
	public List<Booking> bookingsByUser(int idUser) {
	    logger.info("Searching for bookings of user ID: {}", idUser);
	    List<Booking> bookings = bookingRepository.findBookingsByUser(idUser);
	    if (bookings.isEmpty()) {
	        logger.warn("No bookings found for user ID: {}", idUser);
	    }
	    return bookings;
	}

	@Override
	@Transactional
	public void markBookingsAsCompleted(LocalDateTime now) {
	    logger.info("Marking all past bookings as COMPLETED from now: {}", now);
	    bookingRepository.markCompletedBookings(now); 
	}

}
