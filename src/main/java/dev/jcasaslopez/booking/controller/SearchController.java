package dev.jcasaslopez.booking.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.jcasaslopez.booking.dto.StandardResponse;
import dev.jcasaslopez.booking.service.SearchService;

@RestController
public class SearchController {
	
	private SearchService searchService;
	
	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@GetMapping(value="search/availabilityCalendar")
	public ResponseEntity<StandardResponse> availabilityCalendar(int idClassroom, LocalDateTime start, 
			LocalDateTime finish){
		searchService.availabilityCalendarByClassroom(idClassroom, start, finish);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"Availability calendar for classroom " + idClassroom + " retrieved successfully", 
				null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="search/classroomsAvailable")
	public ResponseEntity<StandardResponse> classroomsAvailable(LocalDateTime start, LocalDateTime finish,
			int seats, boolean projector, boolean speakers){
		searchService.classroomsAvailableByPeriodAndFeatures(start, finish, seats, projector, speakers);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"List of classrooms available for the time period retrieved successfully", 
				null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
