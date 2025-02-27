package dev.jcasaslopez.booking.config;

import java.time.LocalTime;

public class OpeningTimesSetting {
	
	private final LocalTime openingTime;
	private final LocalTime closingTime;
	
	public OpeningTimesSetting(LocalTime openingTime, LocalTime closingTime) {
		if(!openingTime.isBefore(closingTime)) {
			 throw new IllegalArgumentException("Opening time must be before closing time");
		}
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public LocalTime getOpeningTime() {
		return openingTime;
	}

	public LocalTime getClosingTime() {
		return closingTime;
	}
	
	@Override
	public String toString() {
	    return "OpeningTimesSetting{" +
	            "openingTime=" + openingTime +
	            ", closingTime=" + closingTime +
	            '}';
	}

}
