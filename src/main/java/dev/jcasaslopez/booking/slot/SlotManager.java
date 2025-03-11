package dev.jcasaslopez.booking.slot;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.SlotDto;

public interface SlotManager {
	
	// Crea un calendario de disponibilidad para un aula en un período de tiempo determinado.
	// 
	// Creates an availability calendar for a classroom within a specified time period.
	List<SlotDto> createCalendar(int idClassroom, LocalDateTime start, LocalDateTime finish);
	
	// Verifica que las aulas estén abiertas durante el período de tiempo especificado.
	//
	// Checks classrooms are open during the specified period of time.
	boolean classroomsOpenDuringPeriod(LocalDateTime start, LocalDateTime finish);
	
	// Verifica que no haya reservas para un aula durante el período de tiempo especificado.
	//
	// Checks there are no bookings for a classroom during the specified period of time.
	boolean classroomAvailableDuringPeriod(int idClassroom, LocalDateTime start, LocalDateTime finish);
	
}
