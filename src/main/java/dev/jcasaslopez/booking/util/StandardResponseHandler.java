package dev.jcasaslopez.booking.util;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.jcasaslopez.booking.dto.StandardResponse;
import jakarta.servlet.http.HttpServletResponse;

// StandardResponseHandler convierte una StandardResponse en una respuesta HTTP estándar, lo que
// es necesario en clases como AuthenticationFilter, donde StandardResponse no puede usarse 
// directamente, ya que las excepciones lanzadas en un filtro no son gestionadas por 
// GlobalExceptionHandler.
// 
// StandardResponseHandler converts a StandardResponse into a standard HTTP response, which 
// is necessary in classes like AuthenticationFilter, where StandardResponse cannot be used 
// directly, as exceptions thrown in a filter are not handled by GlobalExceptionHandler.

@Component
public class StandardResponseHandler {
	
	ObjectMapper objectMapper;

	public StandardResponseHandler() {
//		Crea una instancia de ObjectMapper para convertir objetos Java en JSON y viceversa.
//
//      Creates an instance of ObjectMapper to convert Java objects to JSON and vice versa. 
		this.objectMapper = new ObjectMapper();
		
//		Registra el módulo para manejar correctamente las fechas (LocalDateTime, etc.)
//
//		Registers the module to properly handle date/time types (LocalDateTime, etc.)
		objectMapper.registerModule(new JavaTimeModule());
		
//		Para que las fechas se guarden como texto en formato ISO-8601 (ej. "2024-02-22T15:30:00") 
//
//		Ensures that dates are stored as text in ISO-8601 format (e.g., "2024-02-22T15:30:00")
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
    
    public HttpServletResponse handleResponse(HttpServletResponse response, int status, 
    																String message, String details) throws IOException {
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	response.setStatus(status);
//    	 Crea una StandardResponse con la información proporcionada
//
//    	 Creates a StandardResponse with the provided information.
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), message, details, 
    																HttpStatus.resolve(status));
//    	 Serializa la StandardResponse a formato JSON
//
//    	 Serializes the StandardResponse into JSON format.
    	String jsonResponse = objectMapper.writeValueAsString(respuesta);
    	
//         Escribe la respuesta JSON en el cuerpo de la respuesta HTTP
//
//    	 Writes the JSON response to the HTTP response body.
        response.getWriter().write(jsonResponse);
        return response;
    }   
}