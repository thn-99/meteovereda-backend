package com.weather.station.service;

import com.weather.station.entity.Hour;
import com.weather.station.repository.HourRepository;

import org.springframework.stereotype.Service;

@Service
public class HourService {

    private HourRepository hourRepository;

    public HourService(HourRepository hourRepository){
        this.hourRepository=hourRepository;
    }

    public Hour getByHour(int hour){
        return this.hourRepository.findByHour(hour);
    }
}
