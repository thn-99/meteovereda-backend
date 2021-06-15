package com.weather.station.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.OpenWeatherMinutely;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenWeatherMinutelyRepository extends JpaRepository<OpenWeatherMinutely,Long>{
    OpenWeatherMinutely findTopByOrderByDtDesc();
    List<OpenWeatherMinutely> findByDtBetween(LocalDateTime startDate,LocalDateTime endDate);
}
