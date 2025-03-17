package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.Map;

import dev.jcasaslopez.booking.enums.NotificationType;

public interface NotificationService {

	Map<String, String> messageBuilder(NotificationType notificationType, int idUser, int idClassroom,
			LocalDateTime start, LocalDateTime finish);
	void sendNotification(NotificationType notificationType, int idUser, int idClassroom,
			LocalDateTime start, LocalDateTime finish);
	
}
