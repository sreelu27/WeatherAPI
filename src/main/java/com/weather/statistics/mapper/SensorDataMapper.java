package com.weather.statistics.mapper;

import com.weather.statistics.dto.MetricDto;
import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.entity.Metric;
import com.weather.statistics.entity.Sensor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SensorDataMapper {

    public static RequestDto sensorToSensorDto(Sensor sensor){

        RequestDto requestDto = new RequestDto();
        requestDto.setSensorId(sensor.getSensorId());
        requestDto.setLocation(sensor.getLocation());

        if (sensor.getMetricList() != null) {
            List<MetricDto> metricDtos = sensor.getMetricList().stream()
                    .map(metric -> {
                        MetricDto dto = new MetricDto();
                        dto.setType(metric.getType());
                        dto.setData(metric.getData());
                        dto.setRecordedTime(metric.getRecordedTime());
                        return dto;
                    })
                    .collect(Collectors.toList());
            requestDto.setMetricList(metricDtos);
        } else {
            requestDto.setMetricList(Collections.emptyList());
        }

        return requestDto;

    }

    public static Sensor sensorDtoToSensor(RequestDto requestDto){
        Sensor sensor = new Sensor();
        sensor.setSensorId(requestDto.getSensorId());
        sensor.setLocation(requestDto.getLocation());
        sensor.setRecordedTime(LocalDateTime.now());

        if (requestDto.getMetricList() != null && !requestDto.getMetricList().isEmpty()) {
            List<Metric> metrics = requestDto.getMetricList().stream()
                    .map(metricDto -> {
                        Metric metric = new Metric();
                        metric.setType(metricDto.getType());
                        metric.setData(metricDto.getData());
                        metric.setRecordedTime(LocalDateTime.now());
                        metric.setSensor(sensor);
                        return metric;
                    })
                    .collect(Collectors.toList());

            sensor.setMetricList(metrics);
        } else {
            sensor.setMetricList(new ArrayList<>());
        }
        return sensor;
    }
}
