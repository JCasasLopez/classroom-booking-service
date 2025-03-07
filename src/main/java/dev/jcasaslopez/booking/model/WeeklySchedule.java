package dev.jcasaslopez.booking.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeeklySchedule {
	
	private Map<DayOfWeek, OpeningHours> weeklySchedule;
	
	private static final Logger logger = LoggerFactory.getLogger(WeeklySchedule.class);

	public WeeklySchedule(List<String> weeklyHours) {
		this.weeklySchedule = addDailyOpeningTimes(weeklyHours);
	}

	public WeeklySchedule() {
		super();
	}

	public Map<DayOfWeek, OpeningHours> getWeeklySchedule() {
		return weeklySchedule;
	}

	public void setWeeklySchedule(Map<DayOfWeek, OpeningHours> weeklySchedule) {
		this.weeklySchedule = weeklySchedule;
	}

	// Este método convierte los horarios diarios obtenidos de application.properties
	// en objetos "DailyOpeningTimes". Con estos, crea un objeto "WeeklyOpeningTimes",
	// donde los días de la semana son las claves y los objetos "DailyOpeningTimes"
	// son los valores. Ejemplo: MONDAY → (true, 9:00, 22:00).
	//
	// This method converts the daily schedules obtained from application.properties
	// into "DailyOpeningTimes" objects. Using these, it creates a "WeeklyOpeningTimes" object,
	// where the days of the week are the keys and the "DailyOpeningTimes" objects
	// are the values. Example: MONDAY → (true, 9:00, 22:00).
	public Map<DayOfWeek, OpeningHours> addDailyOpeningTimes(List<String> weeklyHours) {
		// Array con todos los valores de la enumeración "DaysOfWeek".
		//
		// Array that contains all the values of "DaysOfWeek" enumeration.
		DayOfWeek[] daysOfWeek = DayOfWeek.values();
		Map<DayOfWeek, OpeningHours> weeklyOpeningTimes = new HashMap<DayOfWeek, OpeningHours>();

		for (int counter = 0; counter < weeklyHours.size(); counter++) {
			// Obtenemos la cadena de texto que representa el horario de apertura para el día 
			// correspondiente, por ejemplo, "9:00-22:00" o "CLOSED".  
			// Dado que los valores se asignan a "weeklyHours" mediante List.of() (ver OpeningTimesConfig), 
			// el orden de los elementos se mantiene como fueron definidos (de lunes a domingo).
			//
			// We retrieve the string representing the opening hours for the corresponding day,  
			// such as "9:00-22:00" or "CLOSED".  
			// Since the values are assigned to "weeklyHours" using List.of() (see OpeningTimesConfig),
			// the order of the elements is preserved exactly as defined, i.e., from Monday to Sunday.
			String dailyHours = weeklyHours.get(counter);

			// Si ese día está cerrado, directamente instanciamos el objeto "DailyOpeningTimes" 
			// y se añade al Map "weeklyOpeningTimes"
			//
			// If that day is closed, we simply instantiate the "DailyOpeningTimes" object 
			// and add it to the final Map "weeklyOpeningTimes".
			if (dailyHours.equals("CLOSED")) {
				weeklyOpeningTimes.put(daysOfWeek[counter], (new OpeningHours(false, null, null)));
				logger.info("{} is CLOSED", daysOfWeek[counter]);
			} else {
				// Si es día está abierto, hay que parsear la cadena de texto ("9:00-22:00") para
				// convertirla en dos objetos LocalTime, y con ellos, instanciar el objeto
				// "DailyOpeningTimes" que después añadimos al Map "weeklyOpeningTimes".
				//
				// If the day is open, the text string ("9:00-22:00") needs to be parsed to convert it into
				// two LocalTime objects. Using these, we instantiate the "DailyOpeningTimes"
				// object, which is then added to the Map "weeklyOpeningTimes".

				// Validamos que el formato de las horas sea correcto.
				//
				// We validate first that the opening hours format is correct.
				if (!dailyHours.matches("\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}")) {
					throw new IllegalArgumentException(
							"Invalid opening hours format for " + daysOfWeek[counter] + ": " + dailyHours);
				}
				String openingTimeAsString = dailyHours.split("-")[0]; // "9:00"
				int openingHour = Integer.parseInt(openingTimeAsString.split(":")[0]); // 9
				int openingMinute = Integer.parseInt(openingTimeAsString.split(":")[1]); // 00
				LocalTime openingTime = LocalTime.of(openingHour, openingMinute);

				String closingTimeAsString = dailyHours.split("-")[1]; // "22:00"
				int closingHour = Integer.parseInt(closingTimeAsString.split(":")[0]); // 22
				int closingMinute = Integer.parseInt(closingTimeAsString.split(":")[1]); // 00
				LocalTime closingTime = LocalTime.of(closingHour, closingMinute);

				weeklyOpeningTimes.put(daysOfWeek[counter], (new OpeningHours(true, openingTime, closingTime)));
				logger.info("{}: Open from {} to {}", daysOfWeek[counter], openingTime, closingTime);
			}
		}
		return weeklyOpeningTimes;
	}
	
}
