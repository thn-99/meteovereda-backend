package com.weather.station.repository;

import com.weather.station.entity.SourceType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTypeRepository extends JpaRepository<SourceType,Long>{
    
}
