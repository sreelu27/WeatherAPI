package com.weather.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDto {

    private String type;
    private Double data;
    private LocalDateTime recordedTime;
}
