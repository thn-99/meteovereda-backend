package com.weather.station.controller;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weather.station.constant.Constants;
import com.weather.station.entity.StationWeatherMinutely;
import com.weather.station.service.ErrorMinutelyService;
import com.weather.station.service.OpenWeatherMinutelyService;
import com.weather.station.service.StationWeatherMinutelyService;
import com.weather.station.service.TemporaryJsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(Constants.API_WEATHER_V1)
@Api(value = "Sensor Controller")
public class WeatherController {
    @Autowired
    private OpenWeatherMinutelyService openWeatherMinutelyService;

    @Autowired
    private StationWeatherMinutelyService stationWeatherMinutelyService;

    @Autowired
    TemporaryJsonService temporaryJsonService;

    @Autowired
    ErrorMinutelyService errorMinutelyService;

    /**
     * @return responseEntity {@link ResponseEntity}
     * 
     * @throws MethodArgumentNotValidException {@link MethodArgumentNotValidException}
     */
    @ApiOperation(value = "Retrieve current weather", httpMethod = "GET", nickname = "weather")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS"),
            @ApiResponse(code = 500, message = "System error") })
    // @CrossOrigin(origins = "*")
    @GetMapping("/current")
    @CrossOrigin("*")
    public ResponseEntity<Object> getCurrentWeather() {

        StationWeatherMinutely lastSWM = this.stationWeatherMinutelyService.getLast();
        System.out.println("ultimo " + lastSWM);
        HashMap<String, Object> map = new HashMap<>();
        map.put("open", this.temporaryJsonService.getLast());
        map.put("station", lastSWM);
        return ResponseEntity.ok().body(map);
    }

    /**
     * @return responseEntity {@link ResponseEntity}
     * 
     * @throws MethodArgumentNotValidException {@link MethodArgumentNotValidException}
     */
    @ApiOperation(value = "Retrieve Station Weather between dates", httpMethod = "GET", nickname = "weather")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS", response = StationWeatherMinutely[].class),
            @ApiResponse(code = 500, message = "System error") })
    // @CrossOrigin(origins = "*")
    @GetMapping("/station/dates")
    @CrossOrigin("*")
    public ResponseEntity<Object> getStationWeatherBetweenDates(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<StationWeatherMinutely> stationWeatherMinutelies = this.stationWeatherMinutelyService
                .getByDateBetween(startDate, endDate);

        return ResponseEntity.ok().body(stationWeatherMinutelies);
    }

    /**
     * @return responseEntity {@link ResponseEntity}
     * 
     * @throws MethodArgumentNotValidException {@link MethodArgumentNotValidException}
     */
    @ApiOperation(value = "Gets station info", httpMethod = "GET", nickname = "weather")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS", response = StationWeatherMinutely[].class),
            @ApiResponse(code = 500, message = "System error") })
    // @CrossOrigin(origins = "*")
    @GetMapping("/info")
    @CrossOrigin("*")
    public ResponseEntity<Object> getInfo() {

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("station", this.openWeatherMinutelyService.getLast());
        info.put("open", this.openWeatherMinutelyService.getLast());
        info.put("error", this.errorMinutelyService.getLast());
        return ResponseEntity.ok().body(info);
    }

    /**
     * @return responseEntity {@link ResponseEntity}
     * 
     * @throws MethodArgumentNotValidException {@link MethodArgumentNotValidException}
     */
    @ApiOperation(value = "Recreates csv file from database", httpMethod = "GET", nickname = "weather")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "SUCCESS", response = StationWeatherMinutely[].class),
            @ApiResponse(code = 500, message = "System error") })
    // @CrossOrigin(origins = "*")
    @GetMapping("/recreatecsv")
    @CrossOrigin("*")
    public ResponseEntity<Object> recreateCsv() {
        weatherWriter(this.stationWeatherMinutelyService.getAll());
        return ResponseEntity.ok().body("Ok");
    }

    @GetMapping("/csv")
    public ResponseEntity<Resource> getFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateTime = dateFormatter.format(new Date());

        Resource file = getFileAsResource("writeWithCsvBeanWriter.csv");
        HttpHeaders headers = prepareHeaderForFileReturn("weather dump " + currentDateTime + ".csv", request, response);
        return new ResponseEntity<Resource>(file, headers, HttpStatus.OK);
    }

    private HttpHeaders prepareHeaderForFileReturn(String fileName, HttpServletRequest request,
            HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        return headers;
    }

    public Resource getFileAsResource(String filename) throws FileNotFoundException {
        String filePath = "target" + "/" + filename;
        Resource file = loadAsResource(filePath);
        return file;
    }

    private Resource loadAsResource(String filename) throws FileNotFoundException {
        try {
            Path file = Paths.get(filename);
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                // log.error("Could not read file: " + filename);
                throw new FileNotFoundException();
            }
        } catch (MalformedURLException e) {
            // log.error("Could not read file: " + filename, e);
            throw new FileNotFoundException();
        }
    }

    public void weatherWriter(List<StationWeatherMinutely> stationMinutelies) {

        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter("target/writeWithCsvBeanWriter.csv"),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String[] header = new String[] { "dt", "temp", "maxTemp", "maxTempTime", "minTemp", "minTempTime",
                "windDirection", "windSpeed", "humidity", "maxHumidity", "maxHumidityTime", "minHumidity",
                "minHumidityTime", "windChill", "minWindChill", "minWindChillTime", "barometer", "barometerTrend",
                "maxBarometer", "maxBarometerTime", "minBarometer", "minBarometerTime", "dailyRain", "stormyRain",
                "monthlyRain", "totalRain" };

        // write the header
        try {
			beanWriter.writeHeader(header);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        // write the beans
        // beanWriter.write(customer, header, processors);

        for (StationWeatherMinutely stationMinutely : stationMinutelies) {
            try {
                beanWriter.write(stationMinutely, header);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (beanWriter != null) {
            try {
                beanWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
