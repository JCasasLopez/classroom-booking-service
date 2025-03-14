package dev.jcasaslopez.booking.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import dev.jcasaslopez.booking.dto.ClassroomDto;

// Este es un componente provisional con datos hardcodeados para poder continuar con el desarrollo
// mientras el servicio de aulas no está disponible. Se eliminarán los datos hardcodeados y se
// restaurará la conexión con el servicio real cuando esté operativo.
//
// This is a provisional component with hardcoded data to continue development while the
// classroom service is not available. The hardcoded data will be removed and the connection
// with the real service will be restored when it becomes operational.
@Component
public class ClassroomsList {
	
	/* @Autowired
	private RestClient restClient;
	
	private String baseUrl = "http://classroom-service/classroom/getClassroomList"; */
	
	private List<ClassroomDto> classroomList = new ArrayList<>();
	
	public ClassroomsList() {
        this.classroomList = Arrays.asList(
            new ClassroomDto(1, "Aula Magna", 100, true, true),
            new ClassroomDto(2, "Sala de Conferencias", 50, true, false),
            new ClassroomDto(3, "Laboratorio de Informática", 30, false, true),
            new ClassroomDto(4, "Aula de Matemáticas", 20, false, false)
        );
    }

    /*
    @PostConstruct
    
    public void init() {
        updateClassroomList();
    }

    public void updateClassroomList() {
        try {
            ClassroomDto[] response = restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(ClassroomDto[].class);
            if (response != null) {
                this.classroomList = Arrays.asList(response);
            }
        } catch (Exception e) {
        	
            throw new ServiceNotAvailableException("Classroom service did not return a valid response");
        }
    }
    */

	public List<ClassroomDto> getClassroomList() {
		return classroomList;
	}

}
