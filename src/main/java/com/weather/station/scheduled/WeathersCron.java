package com.weather.station.scheduled;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import com.weather.station.entity.Hour;
import com.weather.station.entity.OpenWeatherMinutely;
import com.weather.station.entity.StationWeatherMinutely;
import com.weather.station.entity.TemporaryJson;
import com.weather.station.service.ErrorMinutelyService;
import com.weather.station.service.HourService;
import com.weather.station.service.OpenWeatherMinutelyService;
import com.weather.station.service.StationWeatherMinutelyService;
import com.weather.station.service.TemporaryJsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@Component
@EnableScheduling
public class WeathersCron {

    private final long STATION = 0;
    private final long OPENWEATHER = 1;
    private final long DATE_VIOLATION = 0;
    private final long DATA_ERROR = 1;

    @Autowired
    OpenWeatherMinutelyService openWeatherMinutelyService;

    @Autowired
    StationWeatherMinutelyService stationWeatherMinutelyService;

    @Autowired
    TemporaryJsonService temporaryJsonService;

    @Autowired
    ErrorMinutelyService errorMinutelyService;

    @Autowired
    TelBo telBo;

    @Autowired
    HourService hourService;

    @Value("${OPEN_WEATHER_API}")
    String openApi;

    @Value("${CSV_PATH}")
    String filePath;

    //Every 15 minutes
    @Scheduled(cron = "0 0/15 * * * *")
    public void pruebas() {
        System.out.println("Ejecutando cron");
        double lat = 39;
        double lon = -0.5;
        String openWeatherUrl = "https://api.openweathermap.org/data/2.5/onecall";
        String veredaWeatherStation = "http://meteovereda.ieslavereda.es/datos/plantilla_vacia.htm";

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        TimeZone tz = calendar.getTimeZone();

        ZoneId zoneId = tz.toZoneId();

        LocalDateTime nowTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);

        try {

            HttpResponse<JsonNode> httpResponse = Unirest.get(openWeatherUrl).queryString("lon", lon)
                    .queryString("lat", lat).queryString("units", "metric").queryString("appid", this.openApi).asJson();
            OpenWeatherMinutely owm = new OpenWeatherMinutely(
                    httpResponse.getBody().getObject().getJSONObject("current"), nowTime);

            temporaryJsonService.saveOrUpdate(new TemporaryJson((long) 1, httpResponse.getBody().toString()));

            this.openWeatherMinutelyService.save(owm);

        } catch (Exception e) {
            e.printStackTrace();
            this.errorMinutelyService.newError(nowTime, OPENWEATHER, DATA_ERROR);
        }

        try {
            HttpResponse<String> httpResponse = Unirest.get(veredaWeatherStation).asString();
            StationWeatherMinutely stationWeatherMinutely = new StationWeatherMinutely(httpResponse.getBody(), nowTime);
            weatherWriter(this.stationWeatherMinutelyService.save(stationWeatherMinutely));

        } catch (Exception e) {
            e.printStackTrace();
            this.errorMinutelyService.newError(nowTime, STATION, this.DATA_ERROR);
        }
    
    }

    //On second 15 of every hour
    @Scheduled(cron = "15 0 * * * *")
    public void dailyPredictionSend(){
        Hour hour = this.hourService.getByHour(LocalDateTime.now().getHour());
        this.telBo.dailyPrediction(hour);

    }

    public void weatherWriter(StationWeatherMinutely stationMinutely) {

        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter(filePath+"writeWithCsvBeanWriter.csv", true),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String[] header = new String[] { "dt", "temp", "maxTemp", "maxTempTime", "minTemp", "minTempTime",
                "windDirection", "windSpeed", "humidity", "maxHumidity", "maxHumidityTime", "minHumidity",
                "minHumidityTime", "windChill", "minWindChill", "minWindChillTime", "barometer", "barometerTrend",
                "maxBarometer", "maxBarometerTime", "minBarometer", "minBarometerTime", "dailyRain", "stormyRain",
                "monthlyRain", "totalRain" };

        try {
            beanWriter.write(stationMinutely, header);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (beanWriter != null) {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
