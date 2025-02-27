package dev.jcasaslopez.booking.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DailyOpeningTimes {
	
	private DayOfWeek dayOfWeek;
	private boolean isOpen;
	private LocalTime openingTime;
	private LocalTime closingTime;
	
	public DailyOpeningTimes(DayOfWeek dayOfWeek, boolean isOpen, LocalTime openingTime, 
			LocalTime closingTime) {
		this.dayOfWeek = dayOfWeek;
		this.isOpen = isOpen;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public DailyOpeningTimes() {
		super();
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
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

	@Override
	public String toString() {
		if(!isOpen) {
			return dayOfWeek + ": CLOSED" ;
		}
		return dayOfWeek + ": " + openingTime + "-" + closingTime;
	}
	
}
