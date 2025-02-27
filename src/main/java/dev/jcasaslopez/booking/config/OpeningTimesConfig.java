package dev.jcasaslopez.booking.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.jcasaslopez.booking.model.WeeklyOpeningTimes;

@Configuration
public class OpeningTimesConfig {
	
	// Formatos posibles:
	// "9:00-22:00" (Sin espacios)
	// "CLOSED" (En mayúsculas)

	@Value("${opening-times.monday}") private String mondayHours; 
    @Value("${opening-times.tuesday}") private String tuesdayHours;
    @Value("${opening-times.wednesday}") private String wednesdayHours;
    @Value("${opening-times.thursday}") private String thursdayHours;
    @Value("${opening-times.friday}") private String fridayHours;
    @Value("${opening-times.saturday}") private String saturdayHours; 
    @Value("${opening-times.sunday}") private String sundayHours;
    
    private List<String> weeklyHours = List.of(mondayHours, tuesdayHours, wednesdayHours,
    		thursdayHours, fridayHours, saturdayHours, sundayHours);
    
    @Bean
    WeeklyOpeningTimes weeklyOpeningTimes() {
        return new WeeklyOpeningTimes(weeklyHours);
    }
    
}
