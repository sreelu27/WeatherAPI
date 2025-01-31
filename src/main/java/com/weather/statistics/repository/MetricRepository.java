package com.weather.statistics.repository;

import com.weather.statistics.entity.Metric;
import com.weather.statistics.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {

    Optional<Metric> findBySensorAndTypeAndData(Sensor sensor, String type, Double data);

    List<Metric> findBySensor_SensorIdInAndTypeInAndRecordedTimeBetween(
            List<Long> sensorIds, List<String> metrics, LocalDateTime startDate, LocalDateTime endDate);

}
