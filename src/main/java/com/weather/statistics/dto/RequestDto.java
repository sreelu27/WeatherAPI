package com.weather.statistics.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    @NotNull(message = "Sensor ID is required")
    private Long sensorId;
    @NotNull(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
    @NotNull(message = "Metric list is required")
    private List<MetricDto> metricList;

}
