package dev.jcasaslopez.booking.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WeeklyOpeningTimes {
	
	private List<DailyOpeningTimes> weeklyOpeningTimes;

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
	// A diferencia de "DayOpeningTimes", que usa "LocalTime", este método acepta un "LocalDateTime"
	// porque las reservas están basadas en fechas completas, no solo en horas. 
	// Esto evita que quien llame al método tenga que extraer manualmente el "LocalTime".

	// It determines if the classrooms are open at a given time.
	// Unlike "DayOpeningTimes", which uses "LocalTime", this method accepts a "LocalDateTime"
	// because bookings are based on full dates, not just hours.
	// This prevents the caller from having to manually extract the "LocalTime".
	public boolean isOpen(LocalDateTime givenTime) {
		DayOfWeek dayOfWeek = givenTime.getDayOfWeek();
		LocalTime timeToBeChecked = LocalTime.of(givenTime.getHour(), givenTime.getMinute());
		DailyOpeningTimes dayFound = weeklyOpeningTimes.stream()
				.filter(d -> dayOfWeek == d.getDayOfWeek())
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No such day of the week"));
		if(!dayFound.isOpen() 
				|| timeToBeChecked.isBefore(dayFound.getOpeningTime())
				|| timeToBeChecked.isAfter(dayFound.getClosingTime())) {
			return false;
		}
		return true;
	}
	
	// Este método convierte los horarios diarios sacados de application.properties (ver arriba)
    // en objetos "DailyOpeningTimes" ("Monday", 9:00, 22:00) y con estos, crea el objeto
    // "WeeklyOpeningTimes" que contiene el horario semanal.
	private List<DailyOpeningTimes> addDayOpeningTimes(List<String> weeklyHours) {

		// Array con todos los valores de la enumeración "DaysOfWeek"
		DayOfWeek[] daysOfWeek = DayOfWeek.values();

		//List que representa el horario semanal (es el valor que devuelve el método)
		List<DailyOpeningTimes> weeklyOpeningTimes = new ArrayList<DailyOpeningTimes>();

		for(int counter = 0; counter < weeklyHours.size(); counter ++) {
			//Obtenemos la cadena de texto con las horas para ese día: "9:00-22:00"
			String dailyHours = weeklyHours.get(counter);

			// Si ese día está cerrado, no hace falta parsear las horas de apertura/cierre, directamente
			// instanciamos el objeto "DailyOpeningTimes" y se añade a lista final "weeklyOpeningTimes"
			if(dailyHours.equals("CLOSED")) {
				weeklyOpeningTimes.add(new DailyOpeningTimes(daysOfWeek[counter], false, null, null));

			} else {
				// Si es día está abierto, hay que parsear la cadena de texto ("9:00-22:00") para 
				// convertirla en dos objetos LocalTime, y con ellos, instanciar el objeto 
				// "DailyOpeningTimes" que después añadimos a lista final "weeklyOpeningTimes".
				String openingTimeAsString = dailyHours.split("-")[0]; // "9:00"
				int hourBitOpeningTime = Integer.parseInt(openingTimeAsString.split(":")[0]); // 9
				int minuteBitOpeningTime = Integer.parseInt(openingTimeAsString.split(":")[1]); // 00
				LocalTime openingTime = LocalTime.of(hourBitOpeningTime, minuteBitOpeningTime);

				String closingTimeAsString = dailyHours.split("-")[1];	// "22:00"
				int hourBitClosingTime = Integer.parseInt(closingTimeAsString.split(":")[0]); //22
				int minuteBitClosingTime = Integer.parseInt(closingTimeAsString.split(":")[1]); // 00
				LocalTime closingTime = LocalTime.of(hourBitClosingTime, minuteBitClosingTime);

				weeklyOpeningTimes.add(new DailyOpeningTimes(daysOfWeek[counter], true, openingTime, closingTime));
			}   		
		}
		return weeklyOpeningTimes; 	
	}
	
}
