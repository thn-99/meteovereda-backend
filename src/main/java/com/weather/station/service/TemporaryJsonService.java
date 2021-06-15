package com.weather.station.service;

import com.weather.station.entity.TemporaryJson;

public interface TemporaryJsonService {
    public TemporaryJson getLast();
    public TemporaryJson saveOrUpdate(TemporaryJson temporaryJson);
}
