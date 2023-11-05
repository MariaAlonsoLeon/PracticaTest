package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.time.Instant;

public interface WeatherSupplier {
    Weather getWeather(Location location, Instant instant);
}
