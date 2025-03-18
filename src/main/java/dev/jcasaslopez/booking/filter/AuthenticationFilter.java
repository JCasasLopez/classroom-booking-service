package dev.jcasaslopez.booking.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.jcasaslopez.booking.enums.UserAuthenticationStatus;
import dev.jcasaslopez.booking.exception.FailedAuthenticatedException;
import dev.jcasaslopez.booking.util.StandardResponseHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	private final RestClient restClient;
	private final StandardResponseHandler standardResponseHandler;

	public AuthenticationFilter(RestClient restClient, StandardResponseHandler standardResponseHandler) {
		this.restClient = restClient;
		this.standardResponseHandler = standardResponseHandler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			logger.debug("Entering AuthenticationFilter...");
			String baseUrl = "http://service-user/user/authenticateUser";
			String urlRequested = request.getRequestURL().toString();
			String token = request.getHeader("Authorization");
			
			// Los endpoints "availabilityCalendar" y "classroomsAvailable" son de libre acceso.
			//
			// The "availabilityCalendar" and "classroomsAvailable" endpoints are open access.
			if(urlRequested.endsWith("availabilityCalendar") || urlRequested.endsWith("classroomsAvailable")) {
				logger.info("Open access endpoints");
				filterChain.doFilter(request, response);
				return;
			}
			
			// Permitir un token especial solo para pruebas.
			//
			//	Allow a special token for testing.
			if ("Bearer test-token".equals(token)) {
				logger.info("Using test token for authentication");
				filterChain.doFilter(request, response);
				return;
			}

			// Evitamos hacer una llamada innecesaria al servicio users verificando aquí estas condiciones.
			//
			// If these conditions are met, we can handle them here and avoid an unnecessary call 
			// to the "users" service. 
			if (token == null || !token.startsWith("Bearer ")) {
				logger.warn("Token is missing or does not start with 'Bearer '");
				throw new FailedAuthenticatedException("Invalid authentication token");
			}

			UserAuthenticationStatus userAuthenticationStatus = restClient
					.get()
					.uri(baseUrl)
					.header("Authorization", token)
					.retrieve()
					.body(UserAuthenticationStatus.class);
			logger.info("UserAuthenticationStatus received: {}", userAuthenticationStatus);
			
			switch (userAuthenticationStatus) {
				case FAILED_AUTHENTICATION:
					logger.warn("Invalid or expired token");
					throw new FailedAuthenticatedException("Invalid or expired token");
				case USER_AUTHENTICATED:
					logger.debug("User is authenticated");
					filterChain.doFilter(request, response);
					break;
				default:
					logger.error("Unexpected authentication status: {}", userAuthenticationStatus);
					throw new IllegalStateException("Unexpected value: " + userAuthenticationStatus);
			}
		} catch (FailedAuthenticatedException ex) {
			standardResponseHandler.handleResponse(response, 401, ex.getMessage(), null);
		} catch (IllegalStateException ex) {
			standardResponseHandler.handleResponse(response, 500, ex.getMessage(), null);
		}
	}
}
