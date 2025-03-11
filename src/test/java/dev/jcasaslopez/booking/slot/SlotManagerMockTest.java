package dev.jcasaslopez.booking.slot;

import static org.mockito.Mockito.inOrder;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.jcasaslopez.booking.dto.SlotDto;
import dev.jcasaslopez.booking.entity.Booking;
import dev.jcasaslopez.booking.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
public class SlotManagerMockTest {
	
	@Mock
	private BookingRepository bookingRepository;
	
	// En estos tests, verificamos la implementación en lugar de la interfaz,
	// porque la interfaz solo define el método principal createCalendar().
	// Los demás métodos son auxiliares y específicos de esta implementación.
	//
	// In these tests, we are verifying the implementation rather than the interface,
	// because the interface only defines the main method `createCalendar()`.
	// The other methods are auxiliary and are specific to this implementation.
	@InjectMocks
    private SlotManagerImpl slotManagerImpl; 
	
    @BeforeEach
    void setUp() {
    	// Convertimos la instancia en un Spy. Esto nos permite controlar el comportamiento de 
    	// métodos específicos sin alterar el resto de la implementación.
    	//
    	// We convert the instance into a Spy. This allows us to control specific method behaviors
    	// while keeping the rest of the implementation unchanged.
    	slotManagerImpl = Mockito.spy(slotManagerImpl);
    }
	
	@Test
	@DisplayName("createCalendar works as expected")
	void createCalendar_WhenValidDate_ShouldReturnExpectedResult() {
		// Arrange
		int classroomId = 1;
        LocalDateTime start = LocalDateTime.of(2024, 3, 9, 8, 0);
        LocalDateTime finish = LocalDateTime.of(2024, 3, 9, 18, 0);

        // Usamos el constructor sin parámetros porque esta prueba no valida el resultado final,
        // solo verifica que las llamadas a los métodos ocurran en el orden esperado.
        //
        // We use the parameterless constructor because this test does not validate the final result,
        // only verifies that the method calls occur in the expected order.
        List<Booking> mockBookings = List.of(new Booking());
        List<SlotDto> mockSlots = List.of(new SlotDto());
		
		Mockito.when(bookingRepository.findActiveBookingsForClassroomByPeriod(classroomId, start, finish))
				.thenReturn(mockBookings);
		Mockito.doReturn(mockSlots).when(slotManagerImpl).createEmptyCalendar(classroomId, start, finish);
		Mockito.doReturn(mockSlots).when(slotManagerImpl).updateSlotsAvailability(mockSlots, mockBookings);
		
		// Act
		slotManagerImpl.createCalendar(classroomId, start, finish);
		
		// Assert
		InOrder inOrder = inOrder(bookingRepository, slotManagerImpl);
		inOrder.verify(bookingRepository).findActiveBookingsForClassroomByPeriod(classroomId, start, finish);
		inOrder.verify(slotManagerImpl).createEmptyCalendar(classroomId, start, finish);
		inOrder.verify(slotManagerImpl).updateSlotsAvailability(mockSlots, mockBookings);
    }	
}
