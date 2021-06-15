package com.weather.station.service;

import java.time.LocalDateTime;
import java.util.List;

import com.weather.station.entity.StationWeatherMinutely;
import com.weather.station.repository.StationWeatherMinutelyRepository;

import org.springframework.stereotype.Service;

@Service
public class StationWeatherMinutelyServiceImpl implements StationWeatherMinutelyService{

    private final StationWeatherMinutelyRepository stationWeatherMinutelyRepository;

    public StationWeatherMinutelyServiceImpl(StationWeatherMinutelyRepository stationWeatherMinutelyRepository){
        this.stationWeatherMinutelyRepository=stationWeatherMinutelyRepository;
    }

    @Override
    public StationWeatherMinutely getLast() {
        return this.stationWeatherMinutelyRepository.findTopByOrderByDtDesc();
    }

    @Override
    public List<StationWeatherMinutely> getByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return this.stationWeatherMinutelyRepository.findByDtBetween(startDate, endDate);
    }

    @Override
    public StationWeatherMinutely save(StationWeatherMinutely stationWeatherMinutely) {
        return this.stationWeatherMinutelyRepository.save(stationWeatherMinutely);
        
    }

	@Override
	public List<StationWeatherMinutely> getAll() {
		return this.stationWeatherMinutelyRepository.findAll();
	}


}
