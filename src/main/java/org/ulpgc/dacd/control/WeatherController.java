package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WeatherController {
    private final List<Location> locations;
    private final int days;
    private final WeatherSupplier weatherSupplier;
    private final WeatherStore weatherStore;

    public WeatherController(int days, WeatherSupplier weatherSupplier, WeatherStore weatherStore) {
        this.days = days;
        this.weatherSupplier = weatherSupplier;
        this.weatherStore = weatherStore;
        this.locations = loadLocations();
    }

    public void execute() throws IOException {
        Instant currentTime = Instant.now();
        List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);

        for (Location location : locations) {
            try {
                processLocation(location, forecastTimes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void processLocation(Location location, List<Instant> forecastTimes) {
        try {
            List<Weather> weathers = weatherSupplier.getWeather(location, forecastTimes);
            if (weathers != null && !weathers.isEmpty()) {
                weatherStore.save(weathers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Instant> calculateForecastTimes(Instant currentTime, int days) {
        return IntStream.range(0, days)
                .mapToObj(i -> currentTime.plus(i + 1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS))
                .collect(Collectors.toList());
    }

    private List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("Gran Canaria", 28.11, -15.43));
        locations.add(new Location("Tenerife", 28.46, -16.25));
        locations.add(new Location("La Gomera", 28.09, -17.1));
        locations.add(new Location("La Palma", 28.68, -17.76));
        locations.add(new Location("El Hierro", 27.64, -17.98));
        locations.add(new Location("Fuerteventura", 28.49, -13.86));
        locations.add(new Location("Lanzarote", 28.96, -13.55));
        locations.add(new Location("La Graciosa", 29.23, -13.5));
        return locations;
    }
}
