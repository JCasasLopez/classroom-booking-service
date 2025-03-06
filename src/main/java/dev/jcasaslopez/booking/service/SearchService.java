package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.dto.SlotDto;

public interface SearchService {
	
	// availabilityByClassroom() devuelve una lista de SlotsDto (períodos de 30 minutos) con su disponibilidad, 
	// permitiendo que el frontend represente visualmente la agenda de un aula en un período determinado.
	// Cada SlotDto indicará si está disponible o reservado, lo que permitirá generar una vista de 
	// calendario o cuadrícula en la UI.
	//
	// availabilityByClassroom() returns a list of SlotsDto (30-minute time slots) with their availability, 
	// for the frontend to visually represent the schedule of a classroom for a given period.
	// Each SlotDto will indicate whether it is available or booked, enabling the creation 
	// of a calendar or grid view in the UI.
	//
	// ┌───────────┬───────────┬────────────┬───────────┐
	// │  Time     │  Monday   │  Tuesday   │ Wednesday │ 
	// ├───────────┼───────────┼────────────┼───────────┼
	// │ 10:00     │ Available │   Booked   │ Available │
	// │ 10:30     │   Booked  │ Available  │   Booked  │
	// │ 11:00     │ Available │ Available  │   Booked  │
	// └───────────┴───────────┴────────────┴───────────┴

	List<SlotDto> availabilityByClassroom(LocalDateTime start, LocalDateTime finish);
	List<ClassroomDto> classroomsAvailableByPeriod(LocalDateTime start, LocalDateTime finish);
	List<ClassroomDto> classroomsAvailableByPeriodAndFeatures(LocalDateTime start, LocalDateTime finish,
			int seats, boolean projector, boolean speakers);
}
