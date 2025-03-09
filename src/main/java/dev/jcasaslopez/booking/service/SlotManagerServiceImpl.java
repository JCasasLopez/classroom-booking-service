package dev.jcasaslopez.booking.service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.exception.NoSuchSlotException;
import dev.jcasaslopez.booking.model.OpeningHours;
import dev.jcasaslopez.booking.model.WeeklySchedule;
import dev.jcasaslopez.booking.repository.BookingRepository;

public class SlotManagerServiceImpl implements SlotManagerService {

	private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
	
	@Autowired
	private WeeklySchedule weeklySchedule;
	
	@Autowired
	private BookingRepository bookingRepository;

	// Esta implementación:
	// - Obtiene del repositorio la lista de reservas para un aula y un período determinado.
	// - createEmptyCalendar(): crea la lista de slots "en blanco" (es decir, todos disponibles por 
	// defecto) para el mismo período.
	// - updateSlotsAvailability(): actualiza la lista de slots "en blanco" en función de las reservas.
	//
	// This implementation:
	// - Retrieves the list of bookings for a classroom and a given period from the repository.
	// - createEmptyCalendar(): creates a list of "blank" slots (i.e., all available by default) 
	// for the same period.
	// - updateSlotsAvailability(): updates the "blank" slots list based on the bookings.
	@Override
	public List<SlotDto> createCalendar(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		List<Booking> bookingsByClassroomAndPeriod = bookingRepository.findActiveBookingsForClassroomByPeriod
				(idClassroom, start, finish);
		List<SlotDto> emptyCalendarByClassroom = createEmptyCalendar(idClassroom, start, finish);
		return updateSlotsAvailability(emptyCalendarByClassroom, bookingsByClassroomAndPeriod);
	}

	// Genera una lista de slots de 30 minutos dentro de un período de tiempo especificado.
	// Todos los slots generados estarán inicialmente marcados como disponibles.
	// Reglas de negocio:
	// - Los slots siempre comienzan en una hora en punto o en la media hora.
	// - Todos los slots generados se consideran disponibles hasta que se verifiquen las reservas.
	//
	// Generates a list of 30-minute slots within a specified time period.
	// All generated slots are initially marked as available.
	// Business rules:
	// - Slots always start on the hour or half-hour.
	// - All generated slots are considered available until bookings are verified.
	public List<SlotDto> createEmptyCalendar(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		List<SlotDto> slots = new ArrayList<>();
		logger.info("Starting slot generation for classroom {} from {} to {}", idClassroom, start, finish);
		Map<DayOfWeek, OpeningHours> weeklyScheduleMap = weeklySchedule.getWeeklySchedule();

		// El proceso continúa hasta que la hora límite ("finish") se alcance.
		//
		// The process continues until the finish time is reached.
		for (LocalDateTime slot = start; slot.isBefore(finish);) {
			logger.debug("Processing slots for day: {}", slot.toLocalDate());
			DayOfWeek dayOfWeek = slot.getDayOfWeek();
			LocalTime closingTimeForDay = weeklyScheduleMap.get(dayOfWeek).getClosingTime();
			
			// Mientras el horario actual sea antes del cierre y no supere "finish",
			// se crean slots de 30 minutos en cada iteración.
			//
			// While the current time is before closing time and does not exceed "finish",
			// 30-minute slots are created in each iteration.
			for (; slot.toLocalTime().isBefore(closingTimeForDay) && slot.isBefore(finish); 
					slot = slot.plusMinutes(30)) {
				slots.add(new SlotDto(idClassroom, slot, slot.plusMinutes(30)));
			}
			
			// Cuando se agotan los slots del día actual, saltamos al siguiente día a la hora de apertura.
			//
			// Once all available slots for the current day are generated, it jumps to the next open day
			// at opening time.
			slot = jumpToNextDayAtOpeningTime(slot);
			logger.debug("Jumping to next available day: {}", slot);
		}
		logger.info("Finished slot generation. Total slots created: {}", slots.size());
		return slots;
	}

	// Actualiza la disponibilidad de una lista de slots en base a las reservas activas.
	//
	// Updates the availability of a list of slots based on active bookings.
	public List<SlotDto> updateSlotsAvailability(List<SlotDto> emptyCalendar, List<Booking> bookings) {
		
		// Recorre cada reserva de principio a fin en bloques de 30 minutos.
		//
		// It goes from beginning to end of each booking in 30-minute blocks.
		for(Booking booking:bookings) {
			for(LocalDateTime time = booking.getStart(); time.isBefore(booking.getFinish());
					time = time.plusMinutes(30)) {
				
				// Hace creer al compilador que la variable es efectiva final.
				//
				// Makes the compiler believe that the variable is effectively final.
				LocalDateTime actualTime = time;
				
				// Encuentra el slot del calendario que coincide con el de la 
				// reserva y cambia su disponibilidad a false.
				// 
				// Finds the calendar slot that matches the 30-minute booking block 
				// and changes its availability to false.
				SlotDto slot = emptyCalendar.stream()
						.filter(s -> s.getStart().isEqual(actualTime))
						.findFirst()
						.orElseThrow(() -> new NoSuchSlotException(
							        "Slot not found for time: " + actualTime));
				slot.setAvailable(false);
			}
		}
		return emptyCalendar;
	}
	
	// Método auxiliar de createEmptyCalendar().
	// Devuelve un LocalDateTime con el día siguiente a la hora de apertura.
	// Si el parámetro representa el viernes 7 de marzo a las 21:30 y el lunes abre a las 9:00,
	// el resultado será el lunes 10 de marzo a las 9:00.
	//
	// Auxiliary method for createEmptyCalendar().
	// Returns a LocalDateTime for the next day at opening time.
	// If the input represents Friday, March 7 at 21:30 and Monday opens at 9:00,
	// the result will be Monday, March 10 at 9:00.
	public LocalDateTime jumpToNextDayAtOpeningTime(LocalDateTime time) {
		logger.info("Time passed in: {}", time);
		Map<DayOfWeek, OpeningHours> weeklyScheduleMap = weeklySchedule.getWeeklySchedule();
		OpeningHours openingHours;
		do {
			time = time.plusDays(1);
			openingHours = weeklyScheduleMap.get(time.getDayOfWeek());

			// Si el día evaluado está cerrado (openingTime == null), continúa el bucle hasta
			// encontrar un día abierto.
			//
			// If the evaluated day is closed (openingTime == null), the loop continues until
			// an open day is found.
			if (openingHours.getOpeningTime() == null) {
				logger.debug("Classrooms closed on: {}", time.getDayOfWeek());
			}
		} while (openingHours.getOpeningTime() == null);

		// Construye un LocalDateTime combinando:
		// - La fecha del primer día encontrado en el que las aulas estén abiertas.
		// - La hora de apertura de ese día, obtenida del horario semanal.
		//
		// Builds a LocalDateTime by combining:
		// - The date of the first open day found.
		// - The opening time for that day, retrieved from the weekly schedule.
		LocalTime openingTime = openingHours.getOpeningTime();
		LocalDateTime nextDayAtOpeningTime = time.withHour(openingTime.getHour()).withMinute(openingTime.getMinute());
		logger.info("Time returned: {}", nextDayAtOpeningTime);
		return nextDayAtOpeningTime;
	}

}
