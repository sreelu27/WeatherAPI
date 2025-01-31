package com.weather.statistics.exception;

import com.weather.statistics.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SensorLocationConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSensorLocationConflict(SensorLocationConflictException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("error", "Conflict");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ResponseDto responseDto = new ResponseDto("400", "Validation failed", null);
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        responseDto.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidSensorException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSensorException(InvalidSensorException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Sensor ID Validation Failed");
        response.put("message", "The sensor ID provided is invalid or missing.");

        Map<String, String> details = new HashMap<>();
        details.put("sensorId", "Sensor ID must be a valid, non-null, positive number.");

        response.put("details", details);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStatisticException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatisticException(InvalidStatisticException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "The statistic provided is invalid.");

        Map<String, String> details = new HashMap<>();
        details.put("statistic", "Statistic must be one of the following: 'average', 'min', 'max', 'sum'.");
        response.put("details", details);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDateRangeException(InvalidDateRangeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Date Range Validation Failed");
        response.put("message", "The provided date range is invalid.");

        Map<String, String> details = new HashMap<>();
        details.put("dateRange", "Start date must not be after end date, if both are provided.");

        response.put("details", details);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SensorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSensorNotFound(SensorNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Sensor not found in database");
        response.put("message", "Sensor doesnt exist in database.");

        Map<String, String> details = new HashMap<>();
        details.put("not found", "Sensor doesnt exist in database");

        response.put("details", details);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SomeSensorsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSomeSensorNotFound(SomeSensorsNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Some sensors not found in databse");
        response.put("message", "Sensor doesnt exist in database.");

        Map<String, String> details = new HashMap<>();
        details.put("not found", "Some sensors not found in databse");

        response.put("details", details);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


}
