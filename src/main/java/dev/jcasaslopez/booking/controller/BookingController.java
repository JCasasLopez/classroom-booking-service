package dev.jcasaslopez.booking.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.jcasaslopez.booking.dto.BookingDto;
import dev.jcasaslopez.booking.dto.StandardResponse;
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.service.BookingService;
import jakarta.validation.Valid;

@RestController
public class BookingController {
	
	private BookingService bookingService;
	
	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping(value="/bookings/book", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StandardResponse> book(@Valid @RequestBody BookingDto bookingDto){
		bookingService.book(bookingDto);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"Classroom booked successfully", null, HttpStatus.CREATED);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value="/bookings/cancelBooking")
	public ResponseEntity<StandardResponse> cancelBooking(Long idBooking){
		bookingService.cancel(idBooking, BookingStatus.CANCELLED);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"Booking cancelled successfully", null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value="/bookings/bookingsByUser")
	public ResponseEntity<StandardResponse> bookingsByUser(int idUser){
		bookingService.bookingsByUser(idUser);
		StandardResponse response = new StandardResponse (LocalDateTime.now(), 
				"List of bookings by user retrieved successfully", null, HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
}
