package com.weather.station.service;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.OpenWeatherMinutely;

public interface OpenWeatherMinutelyService {
    public OpenWeatherMinutely getLast();
    public List<OpenWeatherMinutely> getByDateBetween(LocalDateTime startDate,LocalDateTime endDate);
    public OpenWeatherMinutely save(OpenWeatherMinutely openWeatherMinutely);


}
