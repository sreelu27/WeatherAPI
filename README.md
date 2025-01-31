# WeatherAPI
Spring Boot Application WeatherAPI

This project provides an API for storing and retrieving weather metrics for sensors. It allows users to submit weather data, including temperature and humidity readings, and then retrieve statistical data about these metrics based on sensor IDs and dates.

## Features
#### Sensor Creation: 
Create new sensors with location and associated weather metrics.
#### Metrics Storage: 
Store temperature, humidity, and other weather data for sensors.
#### Metric Retrieval: 
Retrieve weather metrics based on sensor IDs and date ranges with different statistical calculations (e.g., average, maximum, minimum).

### Technology Stack
#### Spring Boot: For building the REST API.
#### JPA / Hibernate: For ORM-based database interaction.
#### H2 : Relational databases for data storage.

### API Endpoints
#### 1. Create Sensor and Metrics

Endpoint: /weather/create
   
Method: POST

Request Body:
   
       {
       "sensorId": 1001,
       "location": "London",
       "metricList": [
           {
           "type": "temperature",
           "data": -21.0
           },
           {
           "type": "humidity",
           "data": 52.0
           }
       ]
       }
   Response:
   
       {
       "status": "201",
       "message": "Sensor created successfully",
       "data": {
       "id": 2,
       "sensorId": 1001,
       "location": "London",
           "metricList": [
           {
           "id": 1,
           "type": "temperature",
           "data": -21.0,
           "recordedTime": "2025-01-31T00:15:01"
           },
           {
           "id": 2,
           "type": "humidity",
           "data": 52.0,
           "recordedTime": "2025-01-31T00:15:01"
           }
           ],
       "recordedTime": "2025-01-31T00:15:01"
       }
        }

### Error Response:

400 Bad Request: Invalid sensor ID or location.

409 Conflict: If a sensor with the same ID already exists with a different location.

#### 2. Retrieve Metrics

Endpoint: /weather/metrics

Method: GET

Query Parameters:

sensorIds: Comma-separated list of sensor IDs (e.g., 1001,1002).

metrics: Comma-separated list of metrics (e.g., temperature,humidity).

statistic: The statistic to compute, e.g., average, max, min.

startDateStr: Start date in yyyy-MM-dd'T'HH:mm:ss format.

endDateStr: End date in yyyy-MM-dd'T'HH:mm:ss format.

#### Example Request:

    GET /weather/metrics?sensorIds=1002&metrics=temperature,humidity&statistic=average&startDateStr=2025-02-15T00:00:00&endDateStr=2025-02-16T00:00:00


#### Response

    {
    "status": "200",
    "message": "Metrics retrieved successfully",
    "data": {
    "sensorId": 1002,
        "metrics": [
        {
        "type": "temperature",
        "statistic": "average",
        "value": -21.5
        },
        {
        "type": "humidity",
        "statistic": "average",
        "value": 50.3
        }
        ]
    }
    }

#### Error Response:

##### 400 Bad Request: 
Invalid query parameters or malformed date format.
##### 404 Not Found: 
No data found for the provided sensor IDs or metrics.

#### Validation Errors
   Common errors that may be returned by the API include:

##### 400: 
Invalid request (e.g., malformed dates or invalid parameters).

##### 404: 
Resource not found (e.g., no sensor found for the provided ID).

##### 409: 
Conflict (e.g., sensor location mismatch).

### Database Schema
#### Sensor Table

##### id: Primary key
##### sensorId: Unique identifier for the sensor
##### location: Location of the sensor
##### recordedTime: Timestamp when the sensor data was recorded

#### Metric Table

##### id: Primary key (auto-incremented)
##### type: Type of metric (e.g., temperature, humidity)
##### data: The value of the metric (e.g., -21.0 for temperature)
##### recordedTime: Timestamp when the metric was recorded
##### sensor_id: Foreign key referencing the Sensor table

### How to Run the Project

##### 1. Clone the repository

        git clone https://github.com/yourusername/weather-metrics-api.git
        cd weather-metrics-api


##### 2. Configure Database

Create a database in MySQL or PostgreSQL. I have used in memory DB : H2
Update application.properties (or application.yml if using YAML) to include your database details.

##### 3. Build the project

    mvn clean install

##### 4. Run the project

    mvn spring-boot:run

##### 5. Test the API

Use tools like Postman or curl to make API requests and test the functionality.
The API will be available at http://localhost:8080.







