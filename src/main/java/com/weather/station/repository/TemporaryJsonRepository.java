package com.weather.station.repository;

import com.weather.station.entity.TemporaryJson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryJsonRepository extends JpaRepository<TemporaryJson,Long>{
    
}
