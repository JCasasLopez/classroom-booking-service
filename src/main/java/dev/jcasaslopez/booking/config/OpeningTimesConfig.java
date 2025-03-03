package dev.jcasaslopez.booking.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.jcasaslopez.booking.model.WeeklyOpeningTimes;
import jakarta.annotation.PostConstruct;

@Configuration
public class OpeningTimesConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(OpeningTimesConfig.class);
	
	// Formatos posibles:
	// "9:00-22:00" (Sin espacios)
	// "CLOSED" (En mayúsculas)
	//
	// Possible formats:
	// "9:00-22:00" (No spaces)
	// "CLOSED" (In capital letters)

	@Value("${opening-times.monday}") private String mondayHours; 
    @Value("${opening-times.tuesday}") private String tuesdayHours;
    @Value("${opening-times.wednesday}") private String wednesdayHours;
    @Value("${opening-times.thursday}") private String thursdayHours;
    @Value("${opening-times.friday}") private String fridayHours;
    @Value("${opening-times.saturday}") private String saturdayHours; 
    @Value("${opening-times.sunday}") private String sundayHours;
    
    private List<String> weeklyHours;

    @PostConstruct
    private void init() {
    // Se inicializa aquí porque los valores @Value se asignan después de la inyección de dependencias
    //
    // It is initialized here because the @Value values are assigned after dependency injection.
        weeklyHours = List.of(mondayHours, tuesdayHours, wednesdayHours, 
                              thursdayHours, fridayHours, saturdayHours, sundayHours);
        logger.info("Opening times initialized: {}", weeklyHours);
    }
    
    @Bean
    WeeklyOpeningTimes weeklyOpeningTimes() {
        return new WeeklyOpeningTimes(weeklyHours);
    }
    
}
