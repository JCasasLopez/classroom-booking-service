package dev.jcasaslopez.booking.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class WatchlistDto {
	
	private long idWatchlist;
	@NotNull(message = "idClassroom field is required")
	private int idClassroom;
	@NotNull(message = "idUser field is required")
	private int idUser;
	@NotNull(message = "start field is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
	private LocalDateTime start;
	@NotNull(message = "finish field is required")
    @Future(message = "Finish time must be in the future")
	private LocalDateTime finish;
	private LocalDateTime timestamp;
	
	@AssertTrue(message = "Finish time must be after start time")
	public boolean isFinishAfterStart() {
	    return start != null && finish != null && finish.isAfter(start);
	}

	@AssertTrue(message = "Booking cannot be shorter than 30 minutes or longer than 2 hours")
	public boolean isWithinAllowedDuration() {
	    if (start == null || finish == null) {
	    	// Let @NotNull handle validation
	        return true; 
	    }
	    Duration duration = Duration.between(start, finish);
	    long minutes = duration.toMinutes();
	    return minutes >= 30 && minutes <= 120;
	}

	@AssertTrue(message = "Starting and finishing times must be valid (on the hour or half past)")
	public boolean isStartAndFinishValid() {
	    return (start.getMinute() == 0 || start.getMinute() == 30) 
	        && (finish.getMinute() == 0 || finish.getMinute() == 30);
	}
	
	public WatchlistDto(long idWatchlist, int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish,
			LocalDateTime timestamp) {
		this.idWatchlist = idWatchlist;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
	}

	public WatchlistDto() {
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
