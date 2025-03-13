package dev.jcasaslopez.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClassroomBookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClassroomBookingServiceApplication.class, args);
	}

}
