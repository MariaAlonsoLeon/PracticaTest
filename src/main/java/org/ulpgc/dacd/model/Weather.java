package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final Double temperature;
    private final int humidity;
    private final int clouds;
    private final double windSpeed;
    private final double rain;
    private final Location location;
    private final Instant ts;

    public Weather(Double temperature, int humidity, int clouds, double windSpeed, double rain, Location location, Instant ts) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rain = rain;
        this.location = location;
        this.ts = ts;
    }

    public Double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getClouds() {
        return clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getRain() {
        return rain;
    }

    public Location getLocation() {
        return location;
    }

    public Instant getTs() {
        return ts;
    }
}
