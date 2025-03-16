package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.WatchAlertDto;

public interface WatchAlertService {
	
	WatchAlertDto addWatchAlert(int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish);
	List<WatchAlertDto> watchAlertsListByUser(int idUser);
	List<WatchAlertDto> watchAlertsListByTimePeriodAndClassroom(int idClassroom, LocalDateTime start, 
			LocalDateTime finish);
	
}
