package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.SlotDto;

public interface SlotManagerService {
	
	// Crea un calendario de disponibilidad para un aula en un período de tiempo determinado.
	// 
	// Creates an availability calendar for a classroom within a specified time period.
	List<SlotDto> createCalendar(int idClassroom, LocalDateTime start, LocalDateTime finish);
	
}
