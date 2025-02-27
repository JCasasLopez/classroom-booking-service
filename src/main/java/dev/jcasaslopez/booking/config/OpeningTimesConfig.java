package dev.jcasaslopez.booking.config;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpeningTimesConfig {
	
	@Value("${opening.hours}")
	private LocalTime openingTime;
	
	@Value("${closing.hours}")
	private LocalTime closingTime;
	
	@Bean
	OpeningTimesSetting openingTimesSetting() {
		return new OpeningTimesSetting(openingTime, closingTime);
	}

}
