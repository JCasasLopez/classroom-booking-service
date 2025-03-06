package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.SlotDto;

public interface SlotManagerService {
	
	// Crea un calendario de disponibilidad para un aula en un período de tiempo determinado.
	// 
	// Creates an availability calendar for a classroom within a specified time period.
	List<SlotDto> createCalendar(int idClassroom, LocalDateTime start, LocalDateTime finish);
	
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
	List<SlotDto> createSlots(LocalDateTime start, LocalDateTime finish);
	
    // Actualiza la disponibilidad de una lista de slots en base a las reservas activas.
	// 
	// Updates the availability of a list of slots based on active bookings.  
	List<SlotDto> updateSlotsAvailability();
}
