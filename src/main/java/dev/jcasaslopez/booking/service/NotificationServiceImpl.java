package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import dev.jcasaslopez.booking.enums.NotificationType;
import dev.jcasaslopez.booking.exception.NoSuchClassroomException;
import dev.jcasaslopez.booking.model.ClassroomsList;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	private ClassroomsList classroomsList;
	
	@Autowired
	private RestClient restClient;
	
	@Value("${users.service.url}")
    private String usersServiceUrl;

	@Override
	public Map<String, String> messageBuilder(NotificationType notificationType, int idUser, int idClassroom,
			LocalDateTime start, LocalDateTime finish) {
		Map<String, String> message = new HashMap<>();
        String classroomName = getClassroomName(idClassroom);
        String dateTimeString = formatLocalDateTimesToString(start, finish);

        message.put("Recipient", String.valueOf(idUser));

        switch (notificationType) {
            case BOOK:
            	message.put("Subject", "Booking confirmed");
            	message.put("Message", String.format(
                        "We are pleased to confirm your booking for classroom %s %s.",
                        classroomName, dateTimeString));
                break;
            case WATCH_ALERT:
            	message.put("Subject", "A spot you were watching has opened up!");
            	message.put("Message", String.format(
                        "A booking for classroom %s %s has been cancelled. "
                        + "Hurry up and book it before someone else does!",
                        classroomName, dateTimeString));
                break;
        }
        logger.debug("Generated message: {}", message);
        return message;
    }

	public String getClassroomName(int idClassroom) {
		return classroomsList.getClassroomsList().stream()
							.filter(c -> c.getIdClassroom() == idClassroom)
							.map(c -> c.getName())
							.findFirst()
							.orElseThrow(() -> {
			                    logger.warn("Classroom with ID {} not found", idClassroom);
			                    return new NoSuchClassroomException("Classroom with ID " + idClassroom + " not found");
			                });
	}
	
	public String formatLocalDateTimesToString(LocalDateTime start, LocalDateTime finish) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		return String.format("on %s from %s to %s", 
				start.format(dateFormatter), 
				start.format(timeFormatter),
				finish.format(timeFormatter));
	}
	
	@Override
	public void sendNotification(NotificationType notificationType, int idUser, int idClassroom,
			LocalDateTime start, LocalDateTime finish) {
        String url = usersServiceUrl + "/notifications/send";
		Map<String, String> message = new HashMap<>();
		logger.info("Sending notification to user {} at URL: {}", idUser, url);
		message = messageBuilder(notificationType, idUser, idClassroom, start, finish);
		
		try {
            ResponseEntity<String> response = restClient.post()
                    .uri(url)
                    .body(message)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Notification successfully sent to user {}.", idUser);
            } else {
                logger.error("Failed to send notification to user {}. HTTP Status: {}", idUser, response.getStatusCode());
            }
            
        } catch (Exception ex) {
            logger.error("Error while connecting to the user service: {}", ex.getMessage(), ex);
        }
    }
}