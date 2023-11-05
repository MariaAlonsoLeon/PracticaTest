package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private Double temperature;
    private int humidity;
    private int clouds;
    private double windSpeed;
    private double rain;
    private Location location;
    private Instant ts;

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

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getRain() {
        return rain;
    }

    public void setRain(double rain) {
        this.rain = rain;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }
}
