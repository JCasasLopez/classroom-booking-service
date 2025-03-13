package dev.jcasaslopez.booking.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import dev.jcasaslopez.booking.dto.ClassroomDto;
import dev.jcasaslopez.booking.exception.ServiceNotAvailableException;
import jakarta.annotation.PostConstruct;

@Component
public class ClassroomList {
	
	@Autowired
	private RestClient restClient;
	
	private String baseUrl = "http://classroom-service/classroom/getClassroomList";
	
	private List<ClassroomDto> classroomList = new ArrayList<>();

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

	public List<ClassroomDto> getClassroomList() {
		return classroomList;
	}

}
