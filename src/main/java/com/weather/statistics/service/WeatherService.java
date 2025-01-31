package com.weather.statistics.service;

import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.dto.ResponseDto;
import com.weather.statistics.dto.SensorStatsDto;
import com.weather.statistics.entity.Sensor;
import com.weather.statistics.exception.InvalidDateRangeException;
import com.weather.statistics.exception.InvalidSensorException;
import com.weather.statistics.exception.InvalidStatisticException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface WeatherService {
    ResponseDto sensorData(RequestDto requestDto);
    Map<Long, Map<String, Double>> querySensorData(List<Long> sensorIds, List<String> metrics, String statistic,
                                                   LocalDateTime startDate, LocalDateTime endDate) throws InvalidSensorException, InvalidStatisticException, InvalidDateRangeException;
}
