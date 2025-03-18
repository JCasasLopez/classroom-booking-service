package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.jcasaslopez.booking.dto.WatchAlertDto;
import dev.jcasaslopez.booking.entity.WatchAlert;
import dev.jcasaslopez.booking.exception.NoSuchClassroomException;
import dev.jcasaslopez.booking.mapper.WatchAlertMapper;
import dev.jcasaslopez.booking.model.ClassroomsList;
import dev.jcasaslopez.booking.repository.WatchAlertRepository;

// No validamos la existencia del usuario en estos métodos porque requeriría una llamada a otro 
// microservicio, lo que afectaría el rendimiento y complicaría la mantenibilidad. 
// Si no se devuelven resultados cuando deberían, revisar los logs para verificar posibles IDs incorrectos.
// 
// No se han implementado tests unitarios para esta clase porque no contiene lógica de negocio,  
// solo delega llamadas a otras capas (repositorio y mapeador); estas dependencias 
// ya han sido testeadas exhaustivamente, por lo que tests adicionales serían redundantes.
// 
//
// We do not validate the existence of the user in these methods as it would require a 
// call to another microservice, impacting performance and maintainability.  
// If no results are returned when expected, check the logs to verify possible incorrect IDs.
//
// No unit tests have been implemented for this class as it does not contain business logic,  
// only delegates calls to other layers (repository and mapper).  
// These dependencies have already been thoroughly tested, so additional tests would be redundant.
@Service
public class WatchAlertServiceImpl implements WatchAlertService {
	
	private static final Logger logger = LoggerFactory.getLogger(WatchAlertServiceImpl.class);
	
	private WatchAlertRepository watchAlertRepository;
	private WatchAlertMapper watchAlertMapper;
	private ClassroomsList classroomsList;
	
	public WatchAlertServiceImpl(WatchAlertRepository watchAlertRepository, WatchAlertMapper watchAlertMapper,
			ClassroomsList classroomsList) {
		this.watchAlertRepository = watchAlertRepository;
		this.watchAlertMapper = watchAlertMapper;
		this.classroomsList = classroomsList;
	}

	@Override
	public WatchAlertDto addWatchAlert(WatchAlertDto watchAlertDto) {
		logger.info("Creating watch alert: Classroom ID= {}, User ID= {}, Start= {}, Finish= {}", 
				watchAlertDto.getIdClassroom(), watchAlertDto.getIdUser(), watchAlertDto.getStart(), 
				watchAlertDto.getFinish());
		validateClassroomExists(watchAlertDto.getIdClassroom());
		WatchAlert savedWatchAlert = watchAlertRepository.save(
				watchAlertMapper.WatchAlertDtoToWatchAlert(watchAlertDto));
		logger.info("Watch alert created: Classroom ID= {}, User ID= {}, Start= {}, Finish= {}", 
				savedWatchAlert.getIdClassroom(), savedWatchAlert.getIdUser(), savedWatchAlert.getStart(), 
				savedWatchAlert.getFinish());
		return watchAlertMapper.watchAlertToWatchAlertDto(savedWatchAlert);
	}

	@Override
	public List<WatchAlertDto> watchAlertsListByUser(int idUser) {
		logger.info("Searching watch alerts for user {}", idUser);
		List<WatchAlertDto> watchAlertsByUser = watchAlertRepository.findWatchAlertsByUser(idUser)
				.stream()
				.map(a -> watchAlertMapper.watchAlertToWatchAlertDto(a))
				.toList();
		logger.info("Found {} watch alerts for user {}", watchAlertsByUser.size(), idUser);
		return watchAlertsByUser;
	}

	@Override
	public List<WatchAlertDto> watchAlertsListByTimePeriodAndClassroom(int idClassroom, LocalDateTime start,
			LocalDateTime finish) {
		logger.info("Searching watch alerts for classroom {}, from {} to {}", idClassroom, start, finish);
		validateClassroomExists(idClassroom);
		List<WatchAlertDto> watchAlertsByClassroomAndPeriod = 
				watchAlertRepository.findWatchAlertsByTimePeriodAndClassroom(idClassroom, start, finish)
						.stream()
						.map(a -> watchAlertMapper.watchAlertToWatchAlertDto(a))
						.toList();
		logger.info("Found {} watch alerts for classroom {}, from {} to {}", 
				watchAlertsByClassroomAndPeriod.size(), idClassroom, start, finish);
		return watchAlertsByClassroomAndPeriod;
	}
	
	public void validateClassroomExists(int idClassroom) {
	    if (classroomsList.getClassroomsList()
	            .stream()
	            .noneMatch(c -> c.getIdClassroom() == idClassroom)) {
	        logger.warn("Classroom with ID= {} not found", idClassroom);
	        throw new NoSuchClassroomException("Classroom with ID= " + idClassroom + " not found");
	    }
	}
}
