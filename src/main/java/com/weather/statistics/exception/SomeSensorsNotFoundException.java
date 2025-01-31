package com.weather.statistics.exception;

public class SomeSensorsNotFoundException extends RuntimeException{
    public SomeSensorsNotFoundException(String message) {
        super(message);
    }
}
