package dev.jcasaslopez.booking.service;

import java.util.List;

import dev.jcasaslopez.booking.enums.NotificationType;

public interface NotificationService {

	List<String> messageBuilder(NotificationType notificationType);
	void sendNotification();
	
}
