package com.weather.station.repository;

import com.weather.station.entity.ErrorMinutely;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorMinutelyRepository extends JpaRepository<ErrorMinutely,Long>{
    ErrorMinutely findTopByOrderByDtDesc();
}
