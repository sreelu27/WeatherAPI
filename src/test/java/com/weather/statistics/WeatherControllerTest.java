package com.weather.statistics;

import com.weather.statistics.controller.WeatherController;
import com.weather.statistics.dto.MetricDto;
import com.weather.statistics.dto.RequestDto;
import com.weather.statistics.dto.ResponseDto;
import com.weather.statistics.entity.Metric;
import com.weather.statistics.entity.Sensor;
import com.weather.statistics.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private WeatherService weatherService;
    @InjectMocks
    private WeatherController sensorController;
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(sensorController).build();
    }
    @Test
    void createSensor_success() throws Exception {

        MetricDto metricDto = new MetricDto("Temperature", 22.5, LocalDateTime.now());
        RequestDto sensorDto = new RequestDto();
        sensorDto.setSensorId(1001L);
        sensorDto.setLocation("London");
        sensorDto.setMetricList(List.of(metricDto));

        Sensor sensor = new Sensor();
        sensor.setSensorId(1001L);
        sensor.setLocation("London");
        sensor.setMetricList(List.of(new Metric()));
        sensor.setRecordedTime(LocalDateTime.now());

        ResponseDto mockResponseDto = new ResponseDto("201", "Sensor created successfully", sensor);

        when(weatherService.sensorData(any(RequestDto.class))).thenReturn(mockResponseDto);

        mockMvc.perform(post("/weather/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sensorId\":1001, \"location\":\"London\", \"metricList\":[{\"type\":\"Temperature\",\"data\":22.5, \"recordedTime\":\"2025-01-30T12:00:00\"}]}"))
                .andExpect(status().isCreated())  // Assert: Status is 201 Created
                .andExpect(jsonPath("$.status").value("201"))  // Assert status is 201
                .andExpect(jsonPath("$.message").value("Sensor created successfully"))  // Assert message
                .andExpect(jsonPath("$.data.sensorId").value(1001))  // Assert sensor ID
                .andExpect(jsonPath("$.data.location").value("London"));  // Assert sensor location
    }

    @Test
    void createSensor_missingSensorId() throws Exception {
        String requestBody = "{\"location\":\"London\", \"metricList\":[{\"type\":\"Temperature\",\"data\":22.5, \"recordedTime\":\"2025-01-30T12:00:00\"}]}";

        mockMvc.perform(post("/weather/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSensor_invalidLocation() throws Exception {
        String requestBody = "{\"sensorId\":1001, \"location\":\"L\", \"metricList\":[{\"type\":\"Temperature\",\"data\":22.5, \"recordedTime\":\"2025-01-30T12:00:00\"}]}";  // Invalid location

        mockMvc.perform(post("/weather/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSensor_missingMetricList() throws Exception {
        String requestBody = "{\"sensorId\":1001, \"location\":\"London\"}";

        mockMvc.perform(post("/weather/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSensorMetrics_validRequest() throws Exception {
        Map<Long, Map<String, Double>> mockData = new HashMap<>();
        Map<String, Double> metricMap = new HashMap<>();
        metricMap.put("Temperature", 25.5);
        mockData.put(1001L, metricMap);

        ResponseDto mockResponseDto = new ResponseDto("200", "Metrics fetched successfully", mockData);
        when(weatherService.querySensorData(anyList(), anyList(), anyString(), any(), any())).thenReturn(mockData);
        mockMvc.perform(MockMvcRequestBuilders.get("/weather/metrics")
                        .param("sensorIds", "1001")
                        .param("metrics", "Temperature")
                        .param("statistic", "average"))
                .andExpect(status().isOk())  // Assert: Check that the status is 200 OK
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Metrics fetched successfully"));
    }

    @Test
    void getSensorMetrics_noDataFound() throws Exception {
        Map<Long, Map<String, Double>> emptyData = new HashMap<>();
        when(weatherService.querySensorData(anyList(), anyList(), anyString(), any(), any())).thenReturn(emptyData);

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/metrics")
                        .param("sensorIds", "1001")
                        .param("metrics", "Temperature")
                        .param("statistic", "average"))
                .andExpect(status().isNotFound())  // Assert: Check that the status is 404 Not Found
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value("No data found for the provided sensor IDs"));
    }
    @Test
    void getSensorMetrics_invalidDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/weather/metrics")
                        .param("sensorIds", "1001")
                        .param("metrics", "Temperature")
                        .param("statistic", "average")
                        .param("startDateStr", "2026-01-30T12:00:00")
                        .param("endDateStr", "2025-01-30T12:00:00"))
                .andExpect(status().isNotFound());  // Assert: Status is 400 Bad Request
    }
    @Test
    void getSensorMetrics_missingDateParameters() throws Exception {
        Map<Long, Map<String, Double>> mockData = new HashMap<>();
        Map<String, Double> metricMap = new HashMap<>();
        metricMap.put("Temperature", 22.0);
        mockData.put(1001L, metricMap);

        when(weatherService.querySensorData(anyList(), anyList(), anyString(), any(), any())).thenReturn(mockData);
        mockMvc.perform(MockMvcRequestBuilders.get("/weather/metrics")
                        .param("sensorIds", "1001")
                        .param("metrics", "Temperature")
                        .param("statistic", "average"))
                .andExpect(status().isOk())  // Assert: Status is 200 OK
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Metrics fetched successfully"));
    }

}
