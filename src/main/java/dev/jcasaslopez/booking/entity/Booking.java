package dev.jcasaslopez.booking.entity;

import java.time.LocalDateTime;

import dev.jcasaslopez.booking.enums.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="bookings")
public class Booking {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long idBooking;
	private int idClassroom;
	private int idUser;
	private LocalDateTime start;
	private LocalDateTime finish;
	private LocalDateTime timestamp;
	private String comment;
	private BookingStatus status;

	public Booking(long idBooking, int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish,
			LocalDateTime timestamp, String comment, BookingStatus status) {
		this.idBooking = idBooking;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp;
		this.comment = comment;
		this.status = status;
	}

	public Booking() {
		super();
	}

	public long getIdBooking() {
		return idBooking;
	}

	public void setIdBooking(long idBooking) {
		this.idBooking = idBooking;
	}

	public int getIdClassroom() {
		return idClassroom;
	}

	public void setIdClassroom(int idClassroom) {
		this.idClassroom = idClassroom;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
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
