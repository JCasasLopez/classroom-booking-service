package dev.jcasaslopez.booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.enums.BookingStatus;
import dev.jcasaslopez.booking.exception.OutOfOpeningHoursException;
import dev.jcasaslopez.booking.repository.BookingRepository;

// En los tests usamos Set en lugar de List para asegurarnos de que solo verificamos la presencia
// de los elementos, sin depender de su orden, ya que los métodos probados no tiene por qué  
// devolver los resultados en un orden específico.
//
// Using a Set instead of a List ensures that we are testing only the presence of elements,
// without relying on their order, as the method being tested
// is not expected to return results in a specific sequence.
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchServiceTest {
	
	@Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SearchService searchService;

    private List<Booking> bookings;

    @BeforeAll
    void setUp() {
    	
        bookings = Arrays.asList(
            new Booking(0, 1, 101, 
                LocalDateTime.of(2025, 3, 17, 9, 0),  
                LocalDateTime.of(2025, 3, 17, 11, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            ),
            new Booking(0, 1, 102, 
                LocalDateTime.of(2025, 3, 18, 14, 0), 
                LocalDateTime.of(2025, 3, 18, 16, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            ),
            new Booking(0, 3, 103, 
                LocalDateTime.of(2025, 3, 19, 10, 30), 
                LocalDateTime.of(2025, 3, 19, 12, 30),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            ),
            new Booking(0, 3, 104, 
                LocalDateTime.of(2025, 3, 20, 9, 0), 
                LocalDateTime.of(2025, 3, 20, 10, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            ),
            new Booking(0, 3, 105, 
                LocalDateTime.of(2025, 3, 20, 16, 0), 
                LocalDateTime.of(2025, 3, 20, 18, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.CANCELLED  
            ),
            new Booking(0, 4, 106, 
                LocalDateTime.of(2025, 3, 21, 11, 0), 
                LocalDateTime.of(2025, 3, 21, 13, 0),
                LocalDateTime.now(),
                null,
                BookingStatus.ACTIVE
            )
        );
        bookingRepository.saveAll(bookings);
    }
    
    public static Stream<Arguments> classroomsAvailableByPeriodData() {
        return Stream.of(
            // Caso 1: Aulas disponibles entre 17/03/2025 9:00 y 11:00
            // Case 1: Available classrooms between 17/03/2025 9:00 and 11:00
            Arguments.of(
                LocalDateTime.of(2025, 3, 17, 9, 0),
                LocalDateTime.of(2025, 3, 17, 11, 0),
                Set.of( 
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(3, "103", 30, false, true),
                    new ClassroomDto(4, "104", 20, false, false)
                )
            ),

            // Caso 2: Aulas disponibles entre 19/03/2025 9:00 y 13:00
            // Case 2: Available classrooms between 19/03/2025 9:00 and 13:00
            Arguments.of(
                LocalDateTime.of(2025, 3, 19, 9, 0),
                LocalDateTime.of(2025, 3, 19, 13, 0),
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true),
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(4, "104", 20, false, false)
                )
            ),

            // Caso 3: Aulas disponibles entre 20/03/2025 9:00 y 10:00
            // Case 3: Available classrooms between 20/03/2025 9:00 and 10:00
            Arguments.of(
                LocalDateTime.of(2025, 3, 20, 9, 0),
                LocalDateTime.of(2025, 3, 20, 10, 0),
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true),
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(4, "104", 20, false, false)
                )
            ),

            // Caso 4: Aulas disponibles entre 20/03/2025 16:00 y 18:00
            // Case 4: Available classrooms between 20/03/2025 16:00 and 18:00
            Arguments.of(
                LocalDateTime.of(2025, 3, 20, 16, 0),
                LocalDateTime.of(2025, 3, 20, 18, 0),
                Set.of( 
                    new ClassroomDto(1, "101", 100, true, true),
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(3, "103", 30, false, true),
                    new ClassroomDto(4, "104", 20, false, false)
                )
            ),

            // Caso 5: Aulas disponibles entre 21/03/2025 11:00 y 13:00
            // Case 5: Available classrooms between 21/03/2025 11:00 and 13:00
            Arguments.of(
                LocalDateTime.of(2025, 3, 21, 11, 0),
                LocalDateTime.of(2025, 3, 21, 13, 0),
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true),
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(3, "103", 30, false, true)
                )
            )
        );
    }
   
    @ParameterizedTest
    @MethodSource("classroomsAvailableByPeriodData")
    public void classroomsAvailableByPeriod_shouldReturnCorrectList(LocalDateTime start,
    		LocalDateTime finish, Set<ClassroomDto> classroomsSet) {
    	// Arrange

    	// Act
    	List<ClassroomDto> availableClassrooms = searchService.classroomsAvailableByPeriod(start, finish);
    	Set<ClassroomDto> availableClassroomsAsSet = availableClassrooms.stream()
    			.collect(Collectors.toSet());
    	
    	// Assert
    	assertEquals(classroomsSet, availableClassroomsAsSet, "The list does not contain the expected classrooms");
    }
    
    @Test
    public void classroomsAvailableByPeriod_IfOutOfOpeningHours_ShouldReturnException() {
    	// Arrange
    	LocalDateTime saturdayStart = LocalDateTime.of(2025, 3, 22, 11, 0);
    	LocalDateTime saturdayFinish = LocalDateTime.of(2025, 3, 22, 13, 0);

    	// Act & Assert
    	assertThrows(OutOfOpeningHoursException.class, () -> searchService.classroomsAvailableByPeriod
    			(saturdayStart, saturdayFinish), "A OutOfOpeningHoursException should have been thrown, but wasn't");
    }
    
    public static Stream<Arguments> classroomsAvailableByPeriodAndFeaturesData() {
        return Stream.of(
            // Caso 1: Aulas disponibles entre 17/03/2025 9:00 y 11:00 con aforo ≥30, proyector y sin altavoces
            // Case 1: Available classrooms between 17/03/2025 9:00 and 11:00 with capacity ≥30, projector, and no speakers
            Arguments.of(
                LocalDateTime.of(2025, 3, 17, 9, 0),
                LocalDateTime.of(2025, 3, 17, 11, 0),
                30,  // Aforo mínimo / Minimum required seats
                true,  // Requiere proyector / Requires projector
                false, // No requiere altavoces / Doesn't require speakers
                Set.of(
                    new ClassroomDto(2, "102", 50, true, false) 
                )
            ),

            // Caso 2: Aulas disponibles entre 19/03/2025 9:00 y 13:00 con aforo ≥20, sin proyector ni altavoces
            // Case 2: Available classrooms between 19/03/2025 9:00 and 13:00 with capacity ≥20, no projector, and no speakers
            Arguments.of(
                LocalDateTime.of(2025, 3, 19, 9, 0),
                LocalDateTime.of(2025, 3, 19, 13, 0),
                20,
                false,
                false,
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true), 
                    new ClassroomDto(2, "102", 50, true, false), 
                    new ClassroomDto(4, "104", 20, false, false) 
                )
            ),

            // Caso 3: Aulas disponibles entre 20/03/2025 9:00 y 10:00 con aforo ≥50, proyector y altavoces
            // Case 3: Available classrooms between 20/03/2025 9:00 and 10:00 with capacity ≥50, projector, and speakers
            Arguments.of(
                LocalDateTime.of(2025, 3, 20, 9, 0),
                LocalDateTime.of(2025, 3, 20, 10, 0),
                50,
                true,
                true,
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true) 
                )
            ),

            // Caso 4: Aulas disponibles entre 20/03/2025 16:00 y 18:00 con aforo ≥30, sin proyector y con altavoces
            // Case 4: Available classrooms between 20/03/2025 16:00 and 18:00 with capacity ≥30, no projector, and speakers
            Arguments.of(
            		LocalDateTime.of(2025, 3, 20, 9, 0),
            		LocalDateTime.of(2025, 3, 20, 11, 0),
            		30,
            		false,
            		true,
            		Set.of(new ClassroomDto(1, "101", 100, true, true))
            		),

            // Caso 5: Aulas disponibles entre 21/03/2025 11:00 y 13:00 con aforo ≥10, sin importar proyector ni altavoces
            // Case 5: Available classrooms between 21/03/2025 11:00 and 13:00 with capacity ≥10, any projector, any speakers
            Arguments.of(
                LocalDateTime.of(2025, 3, 21, 11, 0),
                LocalDateTime.of(2025, 3, 21, 13, 0),
                10,
                false, // No importa el proyector / Projector doesn't matter
                false, // No importan los altavoces / Speakers don't matter
                Set.of(
                    new ClassroomDto(1, "101", 100, true, true),
                    new ClassroomDto(2, "102", 50, true, false),
                    new ClassroomDto(3, "103", 30, false, true) 
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("classroomsAvailableByPeriodAndFeaturesData")
    public void classroomsAvailableByPeriodAndFeatures_shouldReturnCorrectList(LocalDateTime start,
    		LocalDateTime finish, int seats, boolean projector, boolean speakers, 
    		Set<ClassroomDto> classroomsSet) {
    	// Arrange

    	// Act
    	List<ClassroomDto> availableClassrooms = searchService.classroomsAvailableByPeriodAndFeatures
    			(start, finish, seats, projector, speakers);
    	Set<ClassroomDto> availableClassroomsAsSet = availableClassrooms.stream()
    			.collect(Collectors.toSet());
    	
    	// Assert
    	assertEquals(classroomsSet, availableClassroomsAsSet, "The list does not contain the expected classrooms");
    }
}
