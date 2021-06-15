package com.weather.station.service;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.OpenWeatherMinutely;
import com.weather.station.repository.OpenWeatherMinutelyRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OpenWeatherMinutelyServiceImpl implements OpenWeatherMinutelyService{
    private OpenWeatherMinutelyRepository openWeatherMinutelyRepository;

    public OpenWeatherMinutelyServiceImpl(OpenWeatherMinutelyRepository openWeatherMinutelyRepository){
        this.openWeatherMinutelyRepository=openWeatherMinutelyRepository;
    }

    @Override
    public OpenWeatherMinutely save(OpenWeatherMinutely openWeatherMinutely) {
        return this.openWeatherMinutelyRepository.save(openWeatherMinutely);
    }

    @Override
    public OpenWeatherMinutely getLast() {
        return this.openWeatherMinutelyRepository.findTopByOrderByDtDesc();
    }

    @Override
    public List<OpenWeatherMinutely> getByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return this.openWeatherMinutelyRepository.findByDtBetween(startDate, endDate);
    }
}
