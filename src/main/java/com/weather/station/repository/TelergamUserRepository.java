package com.weather.station.repository;

import java.util.List;

import com.weather.station.entity.Hour;
import com.weather.station.entity.TelegramUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelergamUserRepository extends JpaRepository<TelegramUser,Long>{

    List<TelegramUser> findByWeatherSuscribeHour(Hour hour); 
    List<TelegramUser> findByAdminTrue();
    
}
