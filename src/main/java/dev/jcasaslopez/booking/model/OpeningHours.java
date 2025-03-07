package dev.jcasaslopez.booking.model;

import java.time.LocalTime;

public class OpeningHours {
	
	private boolean isOpen;
	private LocalTime openingTime;
	private LocalTime closingTime;
	
	// Day open: new DailyOpeningTimes (true, 9:00, 22:00)
	// Day closed: new DailyOpeningTimes (false, null, null)
	public OpeningHours(boolean isOpen, LocalTime openingTime, LocalTime closingTime) {
		this.isOpen = isOpen;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public OpeningHours() {
		super();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public LocalTime getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(LocalTime openingTime) {
		this.openingTime = openingTime;
	}

	public LocalTime getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(LocalTime closingTime) {
		this.closingTime = closingTime;
	}

}
