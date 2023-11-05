package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;
import java.util.Optional;

public interface WeatherStore {
    void save(Weather weather);
    Optional<Weather> loadWeather(Location location, Instant instant);
}
