package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.model.ClassroomsList;
import dev.jcasaslopez.booking.repository.BookingRepository;
import dev.jcasaslopez.booking.slot.SlotManager;

@Service
public class SearchServiceImpl implements SearchService {
	
	private ClassroomsList classroomList;
	private SlotManager slotManager;
	private BookingRepository bookingRepository;

	public SearchServiceImpl(ClassroomsList classroomList, SlotManager slotManager,
			BookingRepository bookingRepository) {
		this.classroomList = classroomList;
		this.slotManager = slotManager;
		this.bookingRepository = bookingRepository;
	}

	@Override
	public List<SlotDto> availabilityCalendarByClassroom(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		return slotManager.createCalendar(idClassroom, start, finish);
	}

	@Override
	public List<ClassroomDto> classroomsAvailableByPeriod(LocalDateTime start, LocalDateTime finish) {
		List<Integer> listsByIdClassroom = bookingRepository.findOccupiedClassroomsbyPeriod(start, finish);
		return classroomList.getClassroomList().stream()
										.filter(c -> !listsByIdClassroom.contains(c.getIdClassroom()))
										.toList();
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
		classroomList.updateClassroomList();
	}*/
}
