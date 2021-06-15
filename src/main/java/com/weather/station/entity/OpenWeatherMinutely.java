package com.weather.station.entity;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "ow_minutely", schema = "weather")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OpenWeatherMinutely {
    @Id
    LocalDateTime dt;
    float temp;
    float feelsLike;
    int pressure;
    int humidity;
    float dewPoint;
    float uvi;
    int clouds;
    int visibility;
    int windSpedd;
    int windDeg;

    public OpenWeatherMinutely(JSONObject jsonData,LocalDateTime dateTime){
        this.dt=dateTime;
        this.temp=jsonData.getFloat("temp");
        this.feelsLike=jsonData.getFloat("feels_like");
        this.pressure=jsonData.getInt("pressure");
        this.humidity=jsonData.getInt("humidity");
        this.dewPoint=jsonData.getFloat("dew_point");
        this.uvi=jsonData.getFloat("uvi");
        this.clouds=jsonData.getInt("clouds");
        this.visibility=jsonData.getInt("visibility");
        this.windSpedd=jsonData.getInt("wind_speed");
        this.windDeg=jsonData.getInt("wind_deg");
    }



}
