package com.weather.station.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
@Table(name = "st_minutely", schema = "weather")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StationWeatherMinutely {
    @Id
    LocalDateTime dt;
    float temp;
    float maxTemp;
    LocalTime maxTempTime;
    float minTemp;
    LocalTime minTempTime;
    String windDirection;
    float windSpeed;
    int humidity;
    int maxHumidity;
    LocalTime maxHumidityTime;
    int minHumidity;
    LocalTime minHumidityTime;
    float windChill;
    float minWindChill;
    LocalTime minWindChillTime;
    float barometer;
    String barometerTrend;
    float maxBarometer;
    LocalTime maxBarometerTime;
    float minBarometer;
    LocalTime minBarometerTime;
    float dailyRain;
    float stormyRain;
    float monthlyRain;
    float totalRain;

    public StationWeatherMinutely(String linesString,LocalDateTime dateTime){
        //linesString.replace(Character.toString(13), "");
        String[] lines = linesString.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            
            lines[i]=lines[i].substring(0, lines[i].length()-1);
            lines[i]=lines[i].replace(" ", "0");
        }

        
        String stDate=lines[0];
        String stTime=lines[1].replace(" ", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        this.dt=LocalDateTime.parse(stDate+" "+stTime,formatter);
        this.dt=dateTime;
        this.temp=Float.parseFloat(lines[2]);
        this.maxTemp=Float.parseFloat(lines[3]);
        this.maxTempTime=LocalTime.parse(lines[4].replace(" ","0"));
        this.minTemp=Float.parseFloat(lines[5]);
        this.minTempTime=LocalTime.parse(lines[6].replace(" ","0"));
        this.windDirection=lines[7];
        this.windSpeed=Float.parseFloat(lines[8]);
        this.humidity=Integer.parseInt(lines[9]);
        this.maxHumidity=Integer.parseInt(lines[10]);
        this.maxHumidityTime=LocalTime.parse(lines[11].replace(" ","0"));
        this.minHumidity=Integer.parseInt(lines[12]);
        this.minHumidityTime=LocalTime.parse(lines[13].replace(" ","0"));
        this.windChill=Float.parseFloat(lines[14]);
        this.minWindChill=Float.parseFloat(lines[15]);
        this.minWindChillTime=LocalTime.parse(lines[16].replace(" ","0"));
        this.barometer=Float.parseFloat(lines[17]);
        this.barometerTrend=lines[18];
        this.maxBarometer=Float.parseFloat(lines[19]);
        this.maxBarometerTime=LocalTime.parse(lines[20].replace(" ","0"));
        this.minBarometer=Float.parseFloat(lines[21]);
        this.minBarometerTime=LocalTime.parse(lines[22].replace(" ","0"));
        this.dailyRain=Float.parseFloat(lines[23]);
        this.stormyRain=Float.parseFloat(lines[24]);
        this.monthlyRain=Float.parseFloat(lines[25]);
        this.totalRain=Float.parseFloat(lines[26]);
    

    }
}
