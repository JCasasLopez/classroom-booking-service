package dev.jcasaslopez.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import dev.jcasaslopez.booking.dto.WatchlistDto;

public interface Watchlist {
	WatchlistDto addWatchlist(int idClassroom, int idUser, LocalDateTime start, LocalDateTime finish);
	List<WatchlistDto> watchlistsByUser(int idUser);
	List<WatchlistDto> watchlistsByTimePeriodAndClassroom(int idClassroom, LocalDateTime start, 
			LocalDateTime finish);
}
