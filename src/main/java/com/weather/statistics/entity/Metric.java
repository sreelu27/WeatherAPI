package com.weather.statistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private Double data;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "sensor_data_id")
    private Sensor sensor;
    @Column(name = "recorded_time")
    private LocalDateTime recordedTime;

}
