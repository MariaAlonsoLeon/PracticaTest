package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeatherStore {
    void save(Weather weather);
    void saveAll(List<Weather> weatherList); // Nuevo método para guardar una lista de Weather
    Optional<Weather> loadWeather(Location location, Instant instant);
}
