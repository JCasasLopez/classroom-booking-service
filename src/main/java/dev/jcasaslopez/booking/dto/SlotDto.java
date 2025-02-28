package dev.jcasaslopez.booking.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

// La clase SlotDto modela un período de media hora para hacer reservas de aulas.  
// Un slot representa un intervalo de tiempo dentro del cual un aula puede ser reservada.  
// Reglas de negocio:  
//	- Las reservas solo pueden hacerse en intervalos de 30 minutos.  
//  - Los slots deben alinearse con las horas en punto o medias horas  
//    (ejemplo válido: 10:00-10:30, 13:30-14:00, 14:00-14:30;  
//     ejemplo inválido: 14:15-15:15).  
//  - Una vez ha empezado el período de tiempo representado por el inicio del slot, este se 
//    considera como no disponible (ver constructor).
// Implementación de Comparable: La clase implementa Comparable<SlotDto> para permitir la ordenación
// natural de los slots en función de su hora de inicio.  

// The SlotDto class models a 30-minute time period for classroom bookings.  
// A slot represents a time interval within which a classroom can be booked.  
// Business rules:  
// 	- Bookings can only be made in 30-minute intervals.  
// 	- Slots must align with full or half-hour marks  
// 	  (valid example: 10:00-10:30, 13:30-14:00, 14:00-14:30;  
// 	   invalid example: 14:15-15:15).  
// 	- Once the time period represented by the slot's start time has begun,  
//    the slot is considered unavailable (see constructor).
// Implementation of Comparable: The class implements Comparable<SlotDto> to allow 
// the natural ordering of slots based on their start time.  

public class SlotDto implements Comparable<SlotDto> {
	
	private int idClassroom;
	@NotNull(message = "start field is required")
	private LocalDateTime start;
	@NotNull(message = "finish field is required")
	private LocalDateTime finish;
	private boolean isAvailable;
	
	@AssertTrue(message = "Finish time must be after start time")
	public boolean isFinishAfterStart() {
	    return start != null && finish != null && finish.isAfter(start);
	}
	
	@AssertTrue(message = "Slot duration must be 30 minutes sharp")
	public boolean isSlotRightDuration() {
	    if (start == null || finish == null) {
	    	// Let @NotNull handle validation
	        return true; 
	    }
	    Duration duration = Duration.between(start, finish);
	    long minutes = duration.toMinutes();
	    return minutes == 30;
	}
	
	@AssertTrue(message = "Starting and finishing times must be valid (on the hour or half past)")
	public boolean isStartAndFinishValid() {
		return (start.getMinute() == 0 || start.getMinute() == 30) 
		        && (finish.getMinute() == 0 || finish.getMinute() == 30);
	}
	
	public SlotDto(int idClassroom, LocalDateTime start, LocalDateTime finish) {
		this.idClassroom = idClassroom;
		this.start = start;
		this.finish = finish;
		if(start.isBefore(LocalDateTime.now())) {
			// Si el slot ya ha comenzado, se marca como no disponible, según las reglas de negocio.
			// If the slot has already started, it is set as not available, as per business rules.
			this.isAvailable = false;
		} else {
			this.isAvailable = true;
		}
	}

	public SlotDto() {
		super();
	}

	public int getIdClassroom() {
		return idClassroom;
	}

	public void setIdClassroom(int idClassroom) {
		this.idClassroom = idClassroom;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getFinish() {
		return finish;
	}

	public void setFinish(LocalDateTime finish) {
		this.finish = finish;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	
	// Compara los slots usando su hora de inicio para permite su ordenación cronológica.
	// Compares slots based on their start time to enable natural ordering.
	@Override
	public int compareTo(SlotDto anotherSlotDto) {
		return this.start.compareTo(anotherSlotDto.start);
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    SlotDto slot = (SlotDto) obj;
	    return idClassroom == slot.idClassroom &&
	           isAvailable == slot.isAvailable &&
	           Objects.equals(start, slot.start) &&
	           Objects.equals(finish, slot.finish);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(idClassroom, start, finish, isAvailable);
	}

	@Override
	public String toString() {
		return "Classroom " + idClassroom + ". Start: " + start + " Finish: " + finish;
	}

}
