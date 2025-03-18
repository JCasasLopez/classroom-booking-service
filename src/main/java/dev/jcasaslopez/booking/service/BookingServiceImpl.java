package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import dev.jcasaslopez.booking.slot.SlotManager;

@Service
public class BookingServiceImpl implements BookingService {
	
	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	
	private BookingRepository bookingRepository;
	private BookingMapper bookingMapper; 
	private SlotManager slotManager;
	private NotificationService notificationService;
	private WatchAlertRepository watchAlertRepository;
	
	public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper, SlotManager slotManager,
			NotificationService notificationService, WatchAlertRepository watchAlertRepository) {
		this.bookingRepository = bookingRepository;
		this.bookingMapper = bookingMapper;
		this.slotManager = slotManager;
		this.notificationService = notificationService;
		this.watchAlertRepository = watchAlertRepository;
	}

	@Override
	@Transactional
	public BookingDto book(BookingDto bookingDto) {
		int idClassroom = bookingDto.getIdClassroom();
		LocalDateTime start = bookingDto.getStart();
		LocalDateTime finish = bookingDto.getFinish();
		
		logger.info("Checking classroom availability: Classroom ID= {}, Start= {}, Finish= {}", 
				idClassroom, start, finish);
		boolean isAvailable = slotManager.isClassroomAvailableDuringPeriod(idClassroom, start, finish);
		
		if(isAvailable) {
			logger.info("Creating new booking: Classroom ID= {}, User ID= {}, Start= {}, Finish= {}", 
					idClassroom, bookingDto.getIdUser(), start, finish);
			Booking returnedBooking = bookingRepository.save(bookingMapper.bookingDtoToBooking(bookingDto));
			logger.info("Booking created: Classroom ID= {}, User ID= {}, Start= {}, Finish= {}", returnedBooking.getIdClassroom(),
					returnedBooking.getIdUser(), returnedBooking.getStart(), returnedBooking.getFinish());
			
			logger.info("Sending booking confirmation notification to User ID= {}", bookingDto.getIdUser());
			notificationService.sendNotification(NotificationType.BOOK, bookingDto.getIdUser(), 
					idClassroom, start, finish);
			
			return bookingMapper.bookingToBookingDto(returnedBooking);
		}
		throw new ClassroomNotAvailableException("Classroom " + idClassroom + 
				" is not available between " + start + " and " + finish);
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
	    
	    // Este método delega el envío de la notificación correspondiente porque, a diferencia de 
	    // book(), dicho envío incluye lógica de negocio.
	    // 
	    // This method delegates the sending of the corresponding notification because, unlike in 
	    // book(), it involves business logic.
	    notifyUsersAboutCancellation(idBooking);
	}
	
	// Encuentra la lista de watch alerts que se ven afectados por la cancelación, y manda la
	// correspondiente notificación al usuario que creó el watch alert.
	// 
	// Find the list of watch alerts affected by the cancellation and send the corresponding 
	// notification to the user who created the watch alert.
	public void notifyUsersAboutCancellation(Long idBooking){		
		Booking cancelledBooking =  bookingRepository.findById(idBooking)
		        .orElseThrow(() -> {
		            logger.warn("Booking not found with ID: {}", idBooking);
		            return new NoSuchBookingException("No such booking or incorrect idBooking");
		        });
		List<WatchAlert> affectedWatchAlerts  = watchAlertRepository.findWatchAlertsByTimePeriodAndClassroom
				(cancelledBooking.getIdClassroom(), cancelledBooking.getStart(), 
						cancelledBooking.getFinish());
		
		if (affectedWatchAlerts.isEmpty()) {
		    logger.info("No watch alerts affected for cancelled booking ID: {}", idBooking);
		} else {
			logger.info("Sending watch alert notifications for cancelled booking ID: {}", idBooking);
		}
		
		for(WatchAlert w:affectedWatchAlerts) {
			notificationService.sendNotification(NotificationType.WATCH_ALERT, w.getIdUser(), 
					w.getIdClassroom(), w.getStart(), w.getFinish());
		}	
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
	// Las reservas ya pasadas se marcan como COMPLETED cada 6 horas automáticamente.
	// 
	// Past bookings are set to COMPLETED automatically every 6 hours.
	@Scheduled(fixedRate = 21_600_00)
	public void markBookingsAsCompleted() {
		LocalDateTime now = LocalDateTime.now();
	    logger.info("Marking all past bookings as COMPLETED from now: {}", now);
	    bookingRepository.markCompletedBookings(now); 
	}

}
