package dev.jcasaslopez.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.jcasaslopez.booking.entity.WatchAlert;

public interface WatchAlertRepository extends JpaRepository<WatchAlert, Long> {

}
