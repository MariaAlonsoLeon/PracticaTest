package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
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

    public void execute() throws IOException {
        for (Location location : locations) {
            Instant currentTime = Instant.now();
            List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);

            List<Weather> weathers = weatherSupplier.getWeather(location, forecastTimes);

            if (weathers != null && !weathers.isEmpty()) {
                weatherStore.save(weathers); // Almacenar todos los datos en la tabla adecuada
            }
        }
    }

    private List<Instant> calculateForecastTimes(Instant currentTime, int days) {
        List<Instant> forecastTimes = new ArrayList<>();
        Instant forecastTime = currentTime;

        for (int i = 0; i < days; i++) {
            forecastTimes.add(forecastTime);
            forecastTime = forecastTime.plusSeconds(24 * 60 * 60); // Avanzar un día (86400 segundos)
        }

        return forecastTimes;
    }

}
