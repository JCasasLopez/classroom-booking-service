package dev.jcasaslopez.booking.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import dev.jcasaslopez.booking.dto.ClassroomDto;

@Component
public class ClassroomList {
	
	@Autowired
	private RestClient restClient;
	
	private String baseUrl = "http://classroom-service/classroom/getClassroomList";
	
	private List<ClassroomDto> classroomList;

	public ClassroomList() {
		this.classroomList = List.of(restClient
			.get()
			.uri(baseUrl)
			.retrieve()
			.body(ClassroomDto[].class));
	}

	public List<ClassroomDto> getClassroomList() {
		return classroomList;
	}

	public void setClassroomList(List<ClassroomDto> classroomList) {
		this.classroomList = classroomList;
	}
	
}
