package dev.jcasaslopez.booking.dto;

import java.time.LocalDateTime;

import dev.jcasaslopez.booking.enums.BookingStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

//Se utilizan tipos envoltorios (Integer, Boolean) en lugar de primitivos, ya que los tipos 
//primitivos no pueden ser null y Jackson asignaría automáticamente valores por defecto 
//(por ejemplo, false para booleanos) al deserializar JSON. Esto impediría detectar campos 
//ausentes y realizar validaciones como @NotNull correctamente.

//Wrapper types (Integer, Boolean) are used instead of primitive types because primitives 
//cannot be null, and Jackson would automatically assign default values (e.g., false for 
//booleans) when deserializing JSON. This would prevent detecting missing fields and 
//correctly performing validations such as @NotNull.

public class BookingDto {
	
	private long idBooking;
	
	@NotNull(message = "idClassroom field is required")
	private Integer idClassroom;
	
	@NotNull(message = "idUser field is required")
	private Integer idUser;
	
	@NotNull(message = "start field is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
	private LocalDateTime start;
	
	@NotNull(message = "finish field is required")
    @Future(message = "Finish time must be in the future")
	private LocalDateTime finish;
	
	private LocalDateTime timestamp;
	private String comment;
	private BookingStatus status;
	
	@AssertTrue(message = "Finish time must be after start time")
	public boolean isFinishAfterStart() {
	    return finish.isAfter(start);
	}
	
	public BookingDto(long idBooking, Integer idClassroom, Integer idUser, LocalDateTime start,
			LocalDateTime finish, LocalDateTime timestamp, String comment, BookingStatus status) {
		this.idBooking = idBooking;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp;
		this.comment = comment;
		this.status = status;
	}

	public BookingDto() {
		super();
	}

	public long getIdBooking() {
		return idBooking;
	}

	public void setIdBooking(long idBooking) {
		this.idBooking = idBooking;
	}

	public Integer getIdClassroom() {
		return idClassroom;
	}

	public void setIdClassroom(Integer idClassroom) {
		this.idClassroom = idClassroom;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
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

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}
	
}
