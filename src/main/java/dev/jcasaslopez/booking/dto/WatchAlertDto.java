package dev.jcasaslopez.booking.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class WatchAlertDto {
	
	private long idWatchAlert;
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
	
	@AssertTrue(message = "Finish time must be after start time")
	public boolean isFinishAfterStart() {
	    return start != null && finish != null && finish.isAfter(start);
	}

	@AssertTrue(message = "WatchAlert has to be exactly 30 minutes")
	public boolean isWithinAllowedDuration() {
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
		return start != null && finish != null 
		        && (start.getMinute() == 0 || start.getMinute() == 30) 
		        && (finish.getMinute() == 0 || finish.getMinute() == 30);
	}
	
	public WatchAlertDto(long idWatchAlert, Integer idClassroom, Integer idUser, LocalDateTime start, LocalDateTime finish,
			LocalDateTime timestamp) {
		this.idWatchAlert = idWatchAlert;
		this.idClassroom = idClassroom;
		this.idUser = idUser;
		this.start = start;
		this.finish = finish;
		this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
	}

	public WatchAlertDto() {
		super();
	}

	public long getIdWatchAlert() {
		return idWatchAlert;
	}

	public void setIdWatchlist(long idWatchAlert) {
		this.idWatchAlert = idWatchAlert;
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

}
