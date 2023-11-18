package org.ulpgc.dacd.model;

import java.time.Instant;

public class Weather {
    private final float temperature;
    private final int humidity;
    private final int clouds;
    private final float windSpeed;
    private final float rain;
    private final Location location;
    private final Instant ts;

    public Weather(float temperature, int humidity, int clouds, float windSpeed, float rain, Location location, Instant ts) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rain = rain;
        this.location = location;
        this.ts = ts;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getClouds() {
        return clouds;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getRain() {
        return rain;
    }

    public Location getLocation() {
        return location;
    }

    public Instant getTs() {
        return ts;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", clouds=" + clouds +
                ", windSpeed=" + windSpeed +
                ", rain=" + rain +
                ", location=" + location +
                ", ts=" + ts +
                '}';
    }
}
