package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class WeatherController {
    private final List<Location> locations;
    private final int days;
    private final WeatherSupplier weatherSupplier;
    private final WeatherStore weatherStore;

    public WeatherController(List<Location> locations, int days, WeatherSupplier weatherSupplier, WeatherStore weatherStore) {
        this.locations = locations;
        this.days = days;
        this.weatherSupplier = weatherSupplier;
        this.weatherStore = weatherStore;
    }

    public void execute() {
        for (Location location : locations) {
            Instant currentTime = Instant.now();

            for (int i = 0; i < days; i++) {
                // Calcular la fecha y hora a las 12 del mediodía para el día actual
                LocalDateTime localDateTime = currentTime.atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .plusDays(i)
                        .atTime(12, 0);
                Instant forecastTime = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

                Weather weather = weatherSupplier.getWeather(location, forecastTime);

                if (weather != null) {
                    weatherStore.save(weather); // Asegurarse de que los datos se almacenen en la tabla adecuada
                }
            }
        }
    }


}