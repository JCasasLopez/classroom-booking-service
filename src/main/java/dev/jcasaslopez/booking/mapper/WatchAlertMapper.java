package dev.jcasaslopez.booking.mapper;

import org.springframework.stereotype.Component;

import dev.jcasaslopez.booking.dto.WatchAlertDto;
import dev.jcasaslopez.booking.entity.WatchAlert;

@Component
public class WatchAlertMapper {
	
	public WatchAlertDto watchAlertToWatchAlertDto(WatchAlert watchAlert) {
		return new WatchAlertDto(watchAlert.getIdWatchAlert(),
				watchAlert.getIdClassroom(),
				watchAlert.getIdUser(),
				watchAlert.getStart(),
				watchAlert.getFinish(),
				watchAlert.getTimestamp());
	}
	
	public WatchAlert WatchAlertDtoToWatchAlert(WatchAlertDto watchAlert) {
		return new WatchAlert(0,
				watchAlert.getIdClassroom(),
				watchAlert.getIdUser(),
				watchAlert.getStart(),
				watchAlert.getFinish(),
				watchAlert.getTimestamp());
	}

}
