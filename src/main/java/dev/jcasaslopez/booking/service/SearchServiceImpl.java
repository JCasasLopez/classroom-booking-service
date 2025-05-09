package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.exception.OutOfOpeningHoursException;
import dev.jcasaslopez.booking.model.ClassroomsList;
import dev.jcasaslopez.booking.repository.BookingRepository;
import dev.jcasaslopez.booking.slot.SlotManager;

@Service
public class SearchServiceImpl implements SearchService {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);
	
	private ClassroomsList classroomsList;
	private SlotManager slotManager;
	private BookingRepository bookingRepository;

	public SearchServiceImpl(ClassroomsList classroomsList, SlotManager slotManager,
			BookingRepository bookingRepository) {
		this.classroomsList = classroomsList;
		this.slotManager = slotManager;
		this.bookingRepository = bookingRepository;
	}

	@Override
	public List<SlotDto> availabilityCalendarByClassroom(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		return slotManager.createCalendar(idClassroom, start, finish);
	}

	@Override
	public List<ClassroomDto> classroomsAvailableByPeriod(LocalDateTime start, LocalDateTime finish) {
		if(!slotManager.isWithinOpeningHours(start, finish)){
			throw new OutOfOpeningHoursException("Classrooms closed from " + start + " to " + finish);
		}
		logger.info("Obtaining list of available classrooms from {} to {}", start, finish);
		List<Integer> listsByIdClassroom = bookingRepository.findOccupiedClassroomsbyPeriod(start, finish);
		List<ClassroomDto> availableClassrooms =  classroomsList.getClassroomsList().stream()
										.filter(c -> !listsByIdClassroom.contains(c.getIdClassroom()))
										.toList();
		logger.info("Found {} available classrooms from {} to {}", availableClassrooms.size(), start, finish);
		return availableClassrooms;
	}

	@Override
	public List<ClassroomDto> classroomsAvailableByPeriodAndFeatures(LocalDateTime start, LocalDateTime finish,
			int seats, boolean projector, boolean speakers) {
		return classroomsAvailableByPeriod(start, finish).stream()
						.filter(c -> c.getSeats() >= seats)
						.filter(c -> projector ? c.getProjector() : true)
						.filter(c -> speakers ? c.getSpeakers() : true)
						.toList();
	}
	
	// Cada 24h llama al servicio "classroom" para obtener la lista actualizada de aulas,
	// a partir de la cual se obtendrá la lista de aulas disponibles en los métodos de este service.
	//
	// Every 24 hours, calls the "classroom" service to get the updated list of classrooms,
	// which will be used to obtain the list of available classrooms in the methods of this service.
	
	/*@Scheduled(fixedRate = 86_400_000)
	public void updateClassroomList() {
		classroomsList.updateClassroomsList();
	}*/
}
