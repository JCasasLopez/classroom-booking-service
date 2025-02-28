package dev.jcasaslopez.booking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="watchlist_alerts")
public class Watchlist {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long idWatchlist;
	private int idClassroom;
	private int idUser;
	private LocalDateTime start;
	private LocalDateTime finish;
	private LocalDateTime timestamp;
	
	public Watchlist(long idWatchlist, int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish,
			LocalDateTime timestamp) {
		this.idWatchlist = idWatchlist;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp;
	}

	public Watchlist() {
		super();
	}

	public long getIdWatchlist() {
		return idWatchlist;
	}

	public void setIdWatchlist(long idWatchlist) {
		this.idWatchlist = idWatchlist;
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
