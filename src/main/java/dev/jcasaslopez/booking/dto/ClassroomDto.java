package dev.jcasaslopez.booking.dto;

import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Se utilizan tipos envoltorios (Integer, Boolean) en lugar de primitivos, ya que los tipos 
// primitivos no pueden ser null y Jackson asignaría automáticamente valores por defecto 
// (por ejemplo, false para booleanos) al deserializar JSON. Esto impediría detectar campos 
// ausentes y realizar validaciones como @NotNull correctamente.

// Wrapper types (Integer, Boolean) are used instead of primitive types because primitives 
// cannot be null, and Jackson would automatically assign default values (e.g., false for 
// booleans) when deserializing JSON. This would prevent detecting missing fields and 
// correctly performing validations such as @NotNull.

public class ClassroomDto {
	
	private int idClassroom;
	@NotBlank(message = "Classroom name is required")
	private String name;
	@NotNull(message = "Seats field is required")
    @Min(value = 8, message = "Seats must be at least 8")
	private Integer seats;
	@NotNull(message = "Projector field is required")
	private Boolean projector;
	@NotNull(message = "Speakers field is required")
	private Boolean speakers;
	
	public ClassroomDto(int idClassroom, String name, Integer seats, Boolean projector, Boolean speakers) {
		this.idClassroom = idClassroom;
		this.name = name;
		this.seats = seats;
		this.projector = projector;
		this.speakers = speakers;
	}

	public ClassroomDto() {
		super();
	}

	public int getIdClassroom() {
		return idClassroom;
	}

	public void setIdClassroom(int idClassroom) {
		this.idClassroom = idClassroom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Boolean getProjector() {
		return projector;
	}

	public void setProjector(Boolean projector) {
		this.projector = projector;
	}

	public Boolean getSpeakers() {
		return speakers;
	}

	public void setSpeakers(Boolean speakers) {
		this.speakers = speakers;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    ClassroomDto that = (ClassroomDto) o;
	    return getIdClassroom() == that.getIdClassroom() &&
	           getSeats() == that.getSeats() &&
	           getProjector() == that.getProjector() &&
	           getSpeakers() == that.getSpeakers() &&
	           Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
	    return Objects.hash(getIdClassroom(), getName(), getSeats(), getProjector(), getSpeakers());
	}
	
}
