package dev.jcasaslopez.booking.mapper;

import org.springframework.stereotype.Component;

import dev.jcasaslopez.booking.dto.WatchlistDto;
import dev.jcasaslopez.booking.entity.Watchlist;

@Component
public class WatchlistMapper {
	
	public WatchlistDto watchlistToWatchlistDto(Watchlist watchlist) {
		return new WatchlistDto(watchlist.getIdWatchlist(),
				watchlist.getIdClassroom(),
				watchlist.getIdUser(),
				watchlist.getStart(),
				watchlist.getFinish(),
				watchlist.getTimestamp());
	}
	
	public Watchlist WatchlistDtoToWatchlist(WatchlistDto watchlist) {
		return new Watchlist(0,
				watchlist.getIdClassroom(),
				watchlist.getIdUser(),
				watchlist.getStart(),
				watchlist.getFinish(),
				watchlist.getTimestamp());
	}

}
