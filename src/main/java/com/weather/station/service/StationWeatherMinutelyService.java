package com.weather.station.service;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.StationWeatherMinutely;

public interface StationWeatherMinutelyService {
    public StationWeatherMinutely getLast();
    public List<StationWeatherMinutely> getByDateBetween(LocalDateTime startDate,LocalDateTime endDate);
    public StationWeatherMinutely save(StationWeatherMinutely stationWeatherMinutely);
    public List<StationWeatherMinutely> getAll();

}
