package com.weather.statistics.controller;

import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.dto.ResponseDto;
import com.weather.statistics.exception.InvalidDateRangeException;
import com.weather.statistics.exception.InvalidSensorException;
import com.weather.statistics.exception.InvalidStatisticException;
import com.weather.statistics.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather")
@Validated
public class WeatherController {

    private WeatherService weatherService;
    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> saveSensorData(@RequestBody @Valid RequestDto requestDto){

        ResponseDto responseDto = weatherService.sensorData(requestDto);
        if ("201".equals(responseDto.getStatus())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else if ("200".equals(responseDto.getStatus())) {
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<ResponseDto> getSensorMetrics(
            @RequestParam List<Long> sensorIds,
            @RequestParam List<String> metrics,
            @RequestParam String statistic,
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr) {

        LocalDateTime startDate;
        LocalDateTime endDate;

            if (startDateStr == null || endDateStr == null) {
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(7);
            } else {
                startDate = parseDate(startDateStr);
                endDate = parseDate(endDateStr);
            }

            Map<Long, Map<String, Double>> result = weatherService.querySensorData(
                    sensorIds, metrics, statistic, startDate, endDate);

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDto("404", "No data found for the provided sensor IDs", result));
            }

            ResponseDto responseDto = new ResponseDto("200", "Metrics fetched successfully", result);
            return ResponseEntity.ok(responseDto);
    }

    public LocalDateTime parseDate(String dateString) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
            }
        }
        throw new DateTimeParseException("Invalid date format", dateString, 0);
    }




}

