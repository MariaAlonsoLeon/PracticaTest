package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API_KEY no configurada. Asegúrate de haberla definido como una variable de entorno.");
            return;
        }
        List<Location> locations = loadLocations();
        WeatherSupplier supplier = new OpenWeatherMapSupplier("https://api.openweathermap.org/data/2.5/forecast?", apiKey);
        WeatherStore store = new SQLiteWeatherStore("jdbc:sqlite:src/main/resources/weather.db");
        WeatherController weatherControl = new WeatherController(locations, 5, supplier, store);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MiTarea(weatherControl), 0, 6 * 60 * 60 * 1000); // Cada 6 horas
        Runtime.getRuntime().addShutdownHook(new Thread(timer::cancel));
    }

    static class MiTarea extends TimerTask {
        private final WeatherController weatherControl;
        private final Logger logger = Logger.getLogger(MiTarea.class.getName());

        public MiTarea(WeatherController weatherControl) {
            this.weatherControl = weatherControl;
        }

        public void run() {
            try {
                logger.info("Iniciando tarea...");
                weatherControl.execute();
                logger.info("Tarea completada.");
            } catch (IOException e) {
                logger.severe("Error durante la ejecución de la tarea: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private static List<Location> loadLocations() {
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
