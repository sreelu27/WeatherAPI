package com.weather.statistics.repository;

import com.weather.statistics.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorId(Long sensorId);

    @Query("SELECT s FROM Sensor s WHERE s.sensorId IN :sensorIds")
    List<Sensor> findSensorsBySensorIds(@Param("sensorIds") List<Long> sensorIds);
}
