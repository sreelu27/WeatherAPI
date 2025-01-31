package com.weather.statistics.service;

import com.weather.statistics.dto.MetricDto;
import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.dto.ResponseDto;
import com.weather.statistics.entity.Metric;
import com.weather.statistics.entity.Sensor;
import com.weather.statistics.exception.*;
import com.weather.statistics.mapper.SensorDataMapper;
import com.weather.statistics.repository.MetricRepository;
import com.weather.statistics.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService{

    private SensorRepository sensorRepository;
    private MetricRepository metricRepository;

    @Autowired
    public WeatherServiceImpl(SensorRepository sensorRepository, MetricRepository metricRepository) {
        this.sensorRepository = sensorRepository;
        this.metricRepository = metricRepository;
    }

    @Override
    public ResponseDto sensorData(RequestDto requestDto) {

        if (requestDto.getSensorId() == null || requestDto.getSensorId() <= 0) {
            throw new IllegalStateException("Sensor ID must be a positive number");
        }
        if (requestDto.getLocation() == null || requestDto.getLocation().isEmpty()) {
            throw new IllegalStateException("Location cannot be null or empty");
        }

        Optional<Sensor> existingSensor = sensorRepository.findBySensorId(requestDto.getSensorId());
        if (existingSensor.isPresent()) {
            Sensor sensor = existingSensor.get();

            if (!sensor.getLocation().equals(requestDto.getLocation())) {
                throw new SensorLocationConflictException("Sensor " + requestDto.getSensorId() +
                        " is already assigned to " + sensor.getLocation() + ", cannot change to " + requestDto.getLocation());
            }

            saveMetrics(sensor, requestDto.getMetricList());
            return new ResponseDto("200", "Sensor already exists, metrics updated", sensor);
        }

        Sensor sensor = SensorDataMapper.sensorDtoToSensor(requestDto);
        Sensor savedSensor = sensorRepository.save(sensor);

        saveMetrics(savedSensor, requestDto.getMetricList());
        return new ResponseDto("201", "Sensor created successfully", savedSensor);
    }

    @Override
    public Map<Long, Map<String, Double>> querySensorData(List<Long> sensorIds, List<String> metrics, String statistic, LocalDateTime startDate, LocalDateTime endDate) throws InvalidSensorException, InvalidStatisticException {

        List<Sensor> sensors = sensorRepository.findSensorsBySensorIds(sensorIds);

        if (sensors.isEmpty()) {
            throw new SensorNotFoundException("No sensors found for the provided sensor IDs: " + sensorIds);
        }

        if (sensors.size() != sensorIds.size()) {
            List<Long> missingSensorIds = sensorIds.stream()
                    .filter(sensorId -> sensors.stream().noneMatch(sensor -> sensor.getSensorId().equals(sensorId)))
                    .collect(Collectors.toList());

            throw new SomeSensorsNotFoundException("Sensors not found for IDs: " + missingSensorIds);
        }

        List<Metric> metricList = metricRepository.findBySensor_SensorIdInAndTypeInAndRecordedTimeBetween(sensorIds, metrics, startDate, endDate);

        Map<Long, Map<String, Double>> result = new HashMap<>();

        for (Metric metric : metricList) {
            Long sensorId = metric.getSensor().getSensorId();
            String metricType = metric.getType();

            result.putIfAbsent(sensorId, new HashMap<>());
            Map<String, Double> metricsMap = result.get(sensorId);

            List<Double> values = metricList.stream()
                    .filter(i -> i.getSensor().getSensorId().equals(sensorId) && i.getType().equals(metricType))
                    .map(i -> i.getData())
                    .collect(Collectors.toList());

            metricsMap.put(metricType, computeStatistics(values, statistic));
        }
        return result;
    }

    private Double computeStatistics(List<Double> values, String statistic) throws InvalidStatisticException {

        switch (statistic.toLowerCase()){
            case "sum" :
                return values.stream().mapToDouble(i -> i).sum();
            case "max" :
                return values.stream().mapToDouble(i -> i).max().orElse(Double.NaN);
            case "min" :
                return values.stream().mapToDouble(i -> i).min().orElse(Double.NaN);
            case "average" :
                return values.stream().mapToDouble(i -> i).average().orElse(0);
            default:
                throw new InvalidStatisticException("Invalid statistic " + statistic);

        }
    }

    private void saveMetrics(Sensor sensor, List<MetricDto> metricList) {
        for (MetricDto metric : metricList) {
            Optional<Metric> existingMetric = metricRepository.findBySensorAndTypeAndData(sensor, metric.getType(), metric.getData());

            if (!existingMetric.isPresent()) {
                Metric m = new Metric();
                m.setType(metric.getType());
                m.setData(metric.getData());
                m.setRecordedTime(LocalDateTime.now());
                m.setSensor(sensor);
                metricRepository.saveAndFlush(m);
            }
        }
    }


}
