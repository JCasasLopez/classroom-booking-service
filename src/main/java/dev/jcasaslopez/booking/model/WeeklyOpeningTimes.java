package dev.jcasaslopez.booking.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeeklyOpeningTimes {
	
	private List<DailyOpeningTimes> weeklyOpeningTimes;
	
	private static final Logger logger = LoggerFactory.getLogger(WeeklyOpeningTimes.class);

	public WeeklyOpeningTimes(List<String> weeklyHours) {
		this.weeklyOpeningTimes = addDayOpeningTimes(weeklyHours);
	}

	public WeeklyOpeningTimes() {
		super();
	}

	public List<DailyOpeningTimes> getWeeklyOpeningTimes() {
		return weeklyOpeningTimes;
	}

	public void setWeeklyOpeningTimes(List<DailyOpeningTimes> weeklyOpeningTimes) {
		this.weeklyOpeningTimes = weeklyOpeningTimes;
	}
	
	// Determina si las aulas están abiertas en un momento dado.
	// A diferencia de "DailyOpeningTimes", que usa "LocalTime", este método acepta un "LocalDateTime"
	// porque las reservas están basadas en fechas completas, no solo en horas. 
	//
	// It determines if the classrooms are open at a given time.
	// Unlike "DailyOpeningTimes", which uses "LocalTime", this method accepts a "LocalDateTime"
	// because bookings are based on full dates, not just hours.
	public boolean isOpen(LocalDateTime givenTime) {
		DayOfWeek dayOfWeek = givenTime.getDayOfWeek();
		LocalTime timeToBeChecked = LocalTime.of(givenTime.getHour(), givenTime.getMinute());
		// Siempre devuelve un valor porque "weeklyOpeningTimes" incluye los 7 días de la semana.
		//
		// Always returns a value because "weeklyOpeningTimes" includes all 7 days of the week.  
		DailyOpeningTimes dayFound = weeklyOpeningTimes.stream()
				.filter(d -> dayOfWeek == d.getDayOfWeek())
				.findFirst()
				.get();
		boolean isOpen = dayFound.isOpen() 
				&& !timeToBeChecked.isBefore(dayFound.getOpeningTime()) 
				&& timeToBeChecked.isBefore(dayFound.getClosingTime());
		logger.info("Checking opening status for {} at {}: {}", dayOfWeek, givenTime, isOpen ? "OPEN" : "CLOSED");
		return isOpen;
	}
	
	// Este método convierte los horarios diarios sacados de application.properties (ver arriba)
    // en objetos "DailyOpeningTimes" ("Monday", 9:00, 22:00) y con estos, crea el objeto
    // "WeeklyOpeningTimes" que contiene el horario semanal.
	//
	// This method converts the daily schedules taken from application.properties (see above)  
	// into "DailyOpeningTimes" objects ("Monday", 9:00, 22:00) and with these,  
	// creates the "WeeklyOpeningTimes" object that contains the weekly schedule.
	private List<DailyOpeningTimes> addDayOpeningTimes(List<String> weeklyHours) {

		// Array con todos los valores de la enumeración "DaysOfWeek".
		//
		// Array that contains all the values of "DaysOfWeek" enumeration. 
		DayOfWeek[] daysOfWeek = DayOfWeek.values();

		// List que representa el horario semanal (es el valor que devuelve el método).
		//
		// List that represents the weekly schedule (this is what the method actually returns).
		List<DailyOpeningTimes> weeklyOpeningTimes = new ArrayList<DailyOpeningTimes>();

		for(int counter = 0; counter < weeklyHours.size(); counter ++) {
			// Obtenemos la cadena de texto con las horas para ese día: "9:00-22:00"
			//
			// We get the string representing that day's opening hours: "9:00-22:00"
			String dailyHours = weeklyHours.get(counter);

			// Si ese día está cerrado, no hace falta parsear las horas de apertura/cierre, directamente
			// instanciamos el objeto "DailyOpeningTimes" y se añade a lista final "weeklyOpeningTimes"
			//
			// If that day is closed, there is no need to parse the opening/closing hours, we simply
			// instantiate the "DailyOpeningTimes" object and add it to the final list "weeklyOpeningTimes".
			if(dailyHours.equals("CLOSED")) {
				weeklyOpeningTimes.add(new DailyOpeningTimes(daysOfWeek[counter], false, null, null));
				logger.info("{} is CLOSED", daysOfWeek[counter]);
			} else {
				// Si es día está abierto, hay que parsear la cadena de texto ("9:00-22:00") para 
				// convertirla en dos objetos LocalTime, y con ellos, instanciar el objeto 
				// "DailyOpeningTimes" que después añadimos a lista final "weeklyOpeningTimes".
				//
				// If the day is open, the text string ("9:00-22:00") needs to be parsed to convert it into
				// two LocalTime objects. Using these, we instantiate the "DailyOpeningTimes" object, 
				// which is then added to the final list "weeklyOpeningTimes".
				
				// Validamos que el formato de las horas sea correcto.
				//
				// We validate first that the opening hours format is correct.
				if (!dailyHours.matches("\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}")) { 
				    throw new IllegalArgumentException("Invalid opening hours format for " + daysOfWeek[counter] + ": " + dailyHours);
				}
				String openingTimeAsString = dailyHours.split("-")[0]; // "9:00"
				int openingHour = Integer.parseInt(openingTimeAsString.split(":")[0]); // 9
				int openingMinute = Integer.parseInt(openingTimeAsString.split(":")[1]); // 00
				LocalTime openingTime = LocalTime.of(openingHour, openingMinute);

				String closingTimeAsString = dailyHours.split("-")[1];	// "22:00"
				int closingHour = Integer.parseInt(closingTimeAsString.split(":")[0]); //22
				int closingMinute = Integer.parseInt(closingTimeAsString.split(":")[1]); // 00
				LocalTime closingTime = LocalTime.of(closingHour, closingMinute);

				weeklyOpeningTimes.add(new DailyOpeningTimes(daysOfWeek[counter], true, openingTime, closingTime));
                logger.info("{}: Open from {} to {}", daysOfWeek[counter], openingTime, closingTime);
			}   		
		}
		return weeklyOpeningTimes; 	
	}
	
}
