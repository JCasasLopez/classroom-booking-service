package dev.jcasaslopez.booking.slot;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.exception.NoSuchSlotException;
import dev.jcasaslopez.booking.model.OpeningHours;
import dev.jcasaslopez.booking.model.WeeklySchedule;
import dev.jcasaslopez.booking.repository.BookingRepository;

@Component
public class SlotManagerImpl implements SlotManager {

	private static final Logger logger = LoggerFactory.getLogger(SlotManagerImpl.class);
	
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
        logger.info("Creating calendar for classroom {} from {} to {}", idClassroom, start, finish);
		List<Booking> bookingsByClassroomAndPeriod = bookingRepository.findActiveBookingsForClassroomByPeriod
				(idClassroom, start, finish);
		List<SlotDto> emptyCalendarByClassroom = createEmptyCalendar(idClassroom, start, finish);
		logger.debug("Found {} bookings for classroom {}", bookingsByClassroomAndPeriod.size(), idClassroom);
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
		List<SlotDto> slotsList = new ArrayList<>();
		WeeklySchedule weeklyScheduleMap = weeklySchedule;
		logger.info("Starting slot generation for classroom {} from {} to {}", idClassroom, start, finish);

		// Nos aseguramos de que start coincida con la hora de apertura, y si ese día está cerrado,
		// con la hora de apertura del siguiente día en que esté abierto.
		start = alignTimeToNextOpeningTime(start, weeklyScheduleMap);
		
		// El proceso continúa hasta que la hora límite ("finish") se alcance.
		//
		// The process continues until the finish time is reached.
		for (LocalDateTime slotStartTime = start; slotStartTime.isBefore(finish);) {
			DayOfWeek dayOfWeek = slotStartTime.getDayOfWeek();
			LocalTime closingTimeForDay = weeklyScheduleMap.getWeeklySchedule().get(dayOfWeek).getClosingTime();
			int counter = 0;
			// Mientras el horario actual sea antes del cierre y no supere "finish",
			// se crean slots de 30 minutos en cada iteración.
			//
			// While the current time is before closing time and does not exceed "finish",
			// 30-minute slots are created in each iteration.
			for (; slotStartTime.toLocalTime().isBefore(closingTimeForDay) && slotStartTime.isBefore(finish); 
					slotStartTime = slotStartTime.plusMinutes(30)) {
				slotsList.add(new SlotDto(idClassroom, slotStartTime, slotStartTime.plusMinutes(30)));
				counter += 1;
			}
			logger.debug("Slots created for day: {}: {}", slotStartTime.toLocalDate(), counter);
			
			// Cuando se agotan los slots del día actual, saltamos al siguiente día a la hora de apertura.
			//
			// Once all available slots for the current day are generated, it jumps to the next open day
			// at opening time.
			slotStartTime = moveToNextDayAtOpeningTime(slotStartTime, weeklyScheduleMap);
		}
		logger.info("Finished slot generation. Total slots created: {}", slotsList.size());
		return slotsList;
	}
	
	// Actualiza la disponibilidad de una lista de slots en base a las reservas activas.
	//
	// Updates the availability of a list of slots based on active bookings.
	public List<SlotDto> updateSlotsAvailability(List<SlotDto> emptyCalendar, List<Booking> bookings) {
        logger.info("Updating slot availability based on {} bookings", bookings.size());

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
	// Devuelve un LocalDateTime con la hora de apertura para ese día, si las aulas están abiertas, o
	// con la hora de apertura para el próximo día en que lo estén.
	//
	// Helper method for createEmptyCalendar().
	// Returns a LocalDateTime with the opening time for that day, if the classrooms are open,
	// or the opening time for the next day where they are.
	public LocalDateTime alignTimeToNextOpeningTime(LocalDateTime time, WeeklySchedule schedule) {
		logger.info("Aligning time {} to next opening time", time);
		DayOfWeek dayOfWeek = time.getDayOfWeek();
		LocalTime openingTimeForDay = schedule.getWeeklySchedule().get(dayOfWeek).getOpeningTime();
		LocalDateTime returnedTime;
		
		// Si ese día está abierto y la hora pasada como parámetro coincide con la de apertura, 
		// se devuelve la hora pasada como parámetro sin modificar.
		//
		// If that day is open and the time passed as a parameter matches the opening time,
		// the time passed as a parameter is returned  unmodified.
		if (openingTimeForDay != null && time.toLocalTime().equals(openingTimeForDay)) {
			returnedTime = time;
			
		// Si ese día está abierto pero la hora pasada como parámetro NO coincide con la de apertura, 
		// se devuelve la hora pasada como parámetro ajustada para que coincida con la de apertura.
		//
		// If that day is open but the time passed as a parameter does NOT match the opening time, 
		// the same day is returned with the time set to the opening time.
		} else if (openingTimeForDay != null){
			returnedTime = time.withHour(openingTimeForDay.getHour()).withMinute(openingTimeForDay.getMinute());
		
		// Si ese día está cerrado, se devuelve la siguiente hora y día en que esté abierto.
		//
		// If that day is closed, the next open day at its opening time is returned.
		} else {
			do {
				time = time.plusDays(1);
				openingTimeForDay = schedule.getWeeklySchedule().get(time.getDayOfWeek()).getOpeningTime();
			} while (openingTimeForDay == null);
			returnedTime = time.withHour(openingTimeForDay.getHour()).withMinute(openingTimeForDay.getMinute());
		}
		logger.info("Aligned time: {}", returnedTime);
		return returnedTime;
	}
	
	// Método auxiliar de createEmptyCalendar().
	// Devuelve un LocalDateTime con la hora de apertura para el próximo día en que las aulas estén abiertas.
	//
	// Helper method for createEmptyCalendar().
	// Returns a LocalDateTime with the opening time for the next day where classrooms are open.
	public LocalDateTime moveToNextDayAtOpeningTime(LocalDateTime time, WeeklySchedule schedule) {
		logger.info("Moving to next open day from time {}", time);
		DayOfWeek dayOfWeek = time.getDayOfWeek();
		LocalTime openingTimeForDay = schedule.getWeeklySchedule().get(dayOfWeek).getOpeningTime();
		do {
			time = time.plusDays(1);
			openingTimeForDay = schedule.getWeeklySchedule().get(time.getDayOfWeek()).getOpeningTime();
		} while (openingTimeForDay == null);
		LocalDateTime returnedTime = time.withHour(openingTimeForDay.getHour()).withMinute(openingTimeForDay.getMinute());
        logger.info("Next available opening time: {}", returnedTime);
		return returnedTime;
	}
	
	@Override
	public boolean isWithinOpeningHours(LocalDateTime start, LocalDateTime finish) {
		logger.info("Checking classrooms are open from {} to {}", start, finish);

		// Primero verificamos que las aulas estén abiertas durante ese período.
		//
		// First of all, we check classrooms are open during the time period.
		DayOfWeek dayOfWeek = start.getDayOfWeek();
		OpeningHours openingHours = weeklySchedule.getWeeklySchedule().get(dayOfWeek);

		// Si ese día está cerrado.
		//
		// If that day is closed.
		if (openingHours.getOpeningTime() == null || openingHours.getClosingTime() == null) {
			logger.info("Classrooms are closed on {}", dayOfWeek);
			return false;
		}

		logger.info("Classrooms are open on {}", dayOfWeek);
		LocalTime startTime = start.toLocalTime();
		LocalTime finishTime = finish.toLocalTime();

		// Si ese día está abierto, verificamos que el período esté dentro del horario de apertura.
		//
		// If that day is open, we check that the time period passed in falls within the opening hours.
		boolean areClassroomsOpen = !startTime.isBefore(openingHours.getOpeningTime()) && startTime.isBefore(openingHours.getClosingTime())
				&& finishTime.isAfter(openingHours.getOpeningTime())
				&& !finishTime.isAfter(openingHours.getClosingTime());
		
		if(areClassroomsOpen) {
			logger.info("Classrooms are open from {} to {}", start, finish);
		} else {
			logger.info("Classrooms are closed from {} to {}", start, finish);
		}
		return areClassroomsOpen;
	}

	@Override
	public boolean isClassroomAvailableDuringPeriod(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		logger.info("Checking availability for classroom {} from {} to {}", idClassroom, start, finish);
		
		// Verificamos primero que las aulas estén abiertas en este período de tiempo.
		//
		// We check first that classrooms are open during that period of time.
		if(!isWithinOpeningHours(start, finish)) {
			return false;
		}
		
		List<Booking> listBookings = bookingRepository.findActiveBookingsForClassroomByPeriod(idClassroom, 
				start, finish);

		// Si la lista está vacía es que no hay ninguna reserva que se solape con
		// esos horarios especificados, luego el aula está disponible.
		//
		// If the list is empty, it means that no bookings overlap the period of time passed in,
		// hence the classroom is available.
		boolean isAvailable = listBookings.isEmpty();
		if(isAvailable) {
			logger.info("Classroom {} is available during the period {} - {}", idClassroom, start, finish);
		} else {
			logger.info("Classroom {} is not available during the period {} - {}", idClassroom, start, finish);
		}
		return isAvailable;
	}
}
