package dev.jcasaslopez.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="watch_alerts")
public class WatchAlert {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long idWatchAlert;
	private int idClassroom;
	private int idUser;
	private LocalDateTime start;
	private LocalDateTime finish;
	private LocalDateTime timestamp;
	
	public WatchAlert(long idWatchAlert, int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish,
			LocalDateTime timestamp) {
		this.idWatchAlert = idWatchAlert;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp;
	}

	public WatchAlert() {
		super();
	}

	public long getidWatchAlert() {
		return idWatchAlert;
	}

	public void setIdWatchAlert(long idWatchAlert) {
		this.idWatchAlert = idWatchAlert;
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
	
}
