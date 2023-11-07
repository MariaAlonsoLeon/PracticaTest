package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public interface WeatherSupplier {
    List<Weather> getWeather(Location location, List<Instant> instants) throws IOException;
}
