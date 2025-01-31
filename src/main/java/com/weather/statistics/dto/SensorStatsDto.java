package com.weather.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorStatsDto {
    private Long sensorId;
    private Map<String, Double> metricStatistics;
}
