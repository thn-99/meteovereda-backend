package com.weather.station.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.StationWeatherMinutely;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationWeatherMinutelyRepository extends JpaRepository<StationWeatherMinutely,Long> {
    StationWeatherMinutely findTopByOrderByDtDesc();
    List<StationWeatherMinutely> findByDtBetween(LocalDateTime startDate,LocalDateTime endDate);

}
