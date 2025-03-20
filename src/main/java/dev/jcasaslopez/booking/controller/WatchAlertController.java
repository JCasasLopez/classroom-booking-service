package dev.jcasaslopez.booking.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.jcasaslopez.booking.dto.StandardResponse;
import dev.jcasaslopez.booking.dto.WatchAlertDto;
import dev.jcasaslopez.booking.service.WatchAlertService;
import jakarta.validation.Valid;

@RestController
public class WatchAlertController {
	
	private WatchAlertService watchAlertService;
	
	public WatchAlertController(WatchAlertService watchAlertService) {
		this.watchAlertService = watchAlertService;
	}

	@PostMapping(value="alerts/createAlert", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StandardResponse> createAlert(@Valid @RequestBody WatchAlertDto watchAlertDto){
		watchAlertService.addWatchAlert(watchAlertDto);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"Watch alert created successfully", null, HttpStatus.CREATED);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping(value="alerts/alertsByUser")
	public ResponseEntity<StandardResponse> alertsByUser(int idUser){
		watchAlertService.watchAlertsListByUser(idUser);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"List of watch alerts by user retrieved successfully", null, HttpStatus.CREATED);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
