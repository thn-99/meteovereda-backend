package com.weather.station.service;

import com.weather.station.entity.TemporaryJson;
import com.weather.station.repository.TemporaryJsonRepository;

import org.springframework.stereotype.Service;

@Service
public class TemporaryJsonServiceImpl implements TemporaryJsonService {

    private final TemporaryJsonRepository temporaryJsonRepository;

    public TemporaryJsonServiceImpl(TemporaryJsonRepository temporaryJsonRepository){
        this.temporaryJsonRepository=temporaryJsonRepository;
    }

    @Override
    public TemporaryJson getLast() {
        return this.temporaryJsonRepository.findById((long) 1).get();
    }

    @Override
    public TemporaryJson saveOrUpdate(TemporaryJson temporaryJson) {
        return this.temporaryJsonRepository.save(temporaryJson);
    }
    
}
