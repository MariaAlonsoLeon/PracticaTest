package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
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
        ExecutorService executor = Executors.newFixedThreadPool(locations.size());
        Instant currentTime = Instant.now();
        List<Instant> forecastTimes = calculateForecastTimes(currentTime, days);
        for (Location location : locations) {
            executor.execute(() -> {
                try {
                    processLocation(location, forecastTimes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
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
        return Stream.iterate(currentTime, time -> time.plusSeconds(86400))
                .limit(days)
                .map(time -> Instant.ofEpochSecond((time.getEpochSecond() + 43200) / 86400 * 86400 + 43200))
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
