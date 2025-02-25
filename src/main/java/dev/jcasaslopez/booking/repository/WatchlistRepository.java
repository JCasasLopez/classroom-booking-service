package dev.jcasaslopez.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.jcasaslopez.booking.entity.Watchlist;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

}
