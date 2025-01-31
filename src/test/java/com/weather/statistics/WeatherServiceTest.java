package com.weather.statistics;

import com.weather.statistics.dto.MetricDto;
import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.dto.ResponseDto;
import com.weather.statistics.entity.Sensor;
import com.weather.statistics.exception.SensorLocationConflictException;
import com.weather.statistics.mapper.SensorDataMapper;
import com.weather.statistics.repository.MetricRepository;
import com.weather.statistics.repository.SensorRepository;
import com.weather.statistics.service.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private WeatherServiceImpl weatherService;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MetricRepository metricRepository;

    @Mock
    private SensorDataMapper sensorDataMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherService).build();
    }

    @Test
    void testSensorData_newSensor() {
        RequestDto requestDto = new RequestDto(1001L, "London", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        Sensor sensor = new Sensor(1L,1001L, "London", List.of(), LocalDateTime.now());
        Sensor savedSensor = new Sensor(1L,1001L, "London", List.of(), LocalDateTime.now());
        ResponseDto expectedResponse = new ResponseDto("201", "Sensor created successfully", savedSensor);
        when(sensorRepository.findBySensorId(1001L)).thenReturn(Optional.empty());
        when(sensorRepository.save(any(Sensor.class))).thenReturn(savedSensor);

        ResponseDto response = weatherService.sensorData(requestDto);

        assertEquals("201", response.getStatus());
        assertEquals("Sensor created successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testSensorData_updateMetrics() {
        RequestDto requestDto = new RequestDto(1001L, "London", List.of(new MetricDto("Temperature", 23.0, LocalDateTime.now())));
        Sensor existingSensor = new Sensor(1L, 1001L, "London", List.of(), LocalDateTime.now());
        ResponseDto expectedResponse = new ResponseDto("200", "Sensor already exists, metrics updated", existingSensor);

        when(sensorRepository.findBySensorId(1001L)).thenReturn(Optional.of(existingSensor));
        ResponseDto response = weatherService.sensorData(requestDto);

        assertEquals("200", response.getStatus());
        assertEquals("Sensor already exists, metrics updated", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testSensorData_invalidSensorId() {
        RequestDto requestDto = new RequestDto(null, "London", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        assertThrows(IllegalStateException.class, () -> weatherService.sensorData(requestDto));
    }

    @Test
    void testSensorData_locationConflict() {
        RequestDto requestDto = new RequestDto(1001L, "New York", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        Sensor existingSensor = new Sensor(1L,1001L, "London", List.of(), LocalDateTime.now());
        when(sensorRepository.findBySensorId(1001L)).thenReturn(Optional.of(existingSensor));

        assertThrows(SensorLocationConflictException.class, () -> weatherService.sensorData(requestDto));
    }

    @Test
    void testSensorData_inValidSensorId() {
        RequestDto invalidRequest = new RequestDto(null, "London", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        assertThrows(IllegalStateException.class, () -> weatherService.sensorData(invalidRequest), "Sensor ID must be a positive number");
    }

    @Test
    void testSensorData_negativeSensorId() {
        RequestDto invalidRequest = new RequestDto(-1L, "London", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        assertThrows(IllegalStateException.class, () -> weatherService.sensorData(invalidRequest), "Sensor ID must be a positive number");
    }
    @Test
    void testSensorData_emptyLocation() {
        RequestDto invalidRequest = new RequestDto(1001L, "", List.of(new MetricDto("Temperature", 22.5, LocalDateTime.now())));
        assertThrows(IllegalStateException.class, () -> weatherService.sensorData(invalidRequest), "Location cannot be null or empty");
    }

    @Test
    void testGetSensorMetrics_notFound() throws Exception {
        mockMvc.perform(get("/weather/metrics"))
                .andExpect(status().isNotFound());
    }


}
