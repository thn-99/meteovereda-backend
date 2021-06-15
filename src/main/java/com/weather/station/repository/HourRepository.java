package com.weather.station.repository;

import com.weather.station.entity.Hour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HourRepository extends JpaRepository<Hour,Long> {
    Hour findByHour(int hour);
}
